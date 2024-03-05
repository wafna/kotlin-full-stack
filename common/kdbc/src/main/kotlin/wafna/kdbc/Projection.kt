package wafna.kdbc

import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaType

/**
 * Encapsulates a projection from a result set to a domain object, T.
 * Provides syntax generation for SQL.
 * Defines operations for reading and writing domain objects.
 * Note that writing is optional.
 */
abstract class Projection<T>(val tableName: String) : RecordReader<T>, BatchWriter<T> {
    abstract val columnNames: List<String>

    fun alias(prefix: String? = null): String = when {
        prefix.isNullOrBlank() -> columnNames
        else -> columnNames.map { "$prefix.$it" }
    }.joinToString(", ")

    fun inList(): String = "(${List(columnNames.size) { "?" }.joinToString(", ")})"

    fun selectSql(alias: String): String = "SELECT ${alias(alias)} FROM $tableName AS $alias"

    fun deleteSql(): String = "DELETE FROM $tableName WHERE"
}

/**
 * Create a projection from the given column names.
 * The given column names must align with the fields of the object, i.e. the data class' primary constructor.
 */
inline fun <reified T : Any> projection(tableName: String, columnNames: List<String>): Projection<T> {
    val type = T::class
    val declaredFields = type.java.declaredFields
    require(declaredFields.size == columnNames.size) {
        "Number of columns (${columnNames.size}) must match number of fields ${declaredFields.size} in $type"
    }
    return projection(T::class, tableName, columnNames)
}

interface FieldNameConverter {
    fun toColumnName(name: String): String
}

/**
 * Create a projection by inferring column names from the object's fields.
 */
inline fun <reified T : Any> projection(tableName: String, fieldNameConverter: FieldNameConverter): Projection<T> {
    val columnNames = T::class.constructors.first()
        .parameters.map { fieldNameConverter.toColumnName(it.name!!) }
    return projection(T::class, tableName, columnNames)
}

@PublishedApi
internal fun <T : Any> projection(
    type: KClass<T>,
    tableName: String,
    columnNames: List<String>
): Projection<T> {
    val ctor = type.constructors.first()
    val ctorParams = ctor.parameters
        .associateBy { it.name!! }
        .mapValues { it.value.type.javaType }
    val ctorParamNames = ctorParams.keys.toList()
    val nameMap = ctorParamNames.zip(columnNames).toMap()
    return object : Projection<T>(tableName) {
        override val columnNames: List<String> = columnNames
        override fun read(resultSet: ResultSet): T {
            val values = ctorParamNames.map { paramName ->
                // We need to match on the java type for primitives.
                when (ctorParams[paramName]!!) {
                    Int::class.java ->
                        resultSet.getInt(nameMap[paramName]!!)

                    Long::class.java ->
                        resultSet.getLong(nameMap[paramName]!!)

                    String::class.java ->
                        resultSet.getString(nameMap[paramName]!!)

                    Boolean::class.java ->
                        resultSet.getBoolean(nameMap[paramName]!!)

                    Float::class.java ->
                        resultSet.getFloat(nameMap[paramName]!!)

                    Double::class.java ->
                        resultSet.getDouble(nameMap[paramName]!!)

                    // Types not "masked" by Kotlin.
                    else ->
                        resultSet.getObject(nameMap[paramName]!!)
                }
            }
            return ctor.call(*values.toTypedArray())
        }

        override fun write(record: T): List<Any> =
            ctorParamNames.map { paramName ->
                record.javaClass.getDeclaredField(paramName).run {
                    isAccessible = true
                    get(record)
                }
            }
    }
}
