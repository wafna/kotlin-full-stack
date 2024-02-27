package wafna.kdbc

import java.sql.ResultSet
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaType

abstract class Projection<T>(val tableName: String) {
    abstract val columnNames: List<String>

    fun alias(prefix: String? = null): String = when {
        prefix.isNullOrBlank() -> columnNames
        else -> columnNames.map { "$prefix.$it" }
    }.joinToString(", ")

    fun inList(): String = "(${List(columnNames.size) { "?" }.joinToString(", ")})"

    abstract fun read(resultSet: ResultSet): T

    abstract fun write(value: T): List<Any>
}

interface FieldNameConverter {
    fun toColumnName(name: String): String
}

inline fun <reified T : Any> projection(tableName: String, caseConverter: FieldNameConverter): Projection<T> =
    projection(T::class, tableName, caseConverter)

@PublishedApi
internal fun <T : Any> projection(
    type: KClass<T>,
    tableName: String,
    caseConverter: FieldNameConverter
): Projection<T> {
    val ctor = type.constructors.first()
    val ctorParams = ctor.parameters
        .associateBy { it.name!! }
        .mapValues { it.value.type.javaType }
    val ctorParamNames = ctorParams.keys.toList()
    val columns = ctorParamNames.map { caseConverter.toColumnName(it) }
    val nameMap = ctorParamNames.zip(columns).toMap()
    return object : Projection<T>(tableName) {
        override val columnNames: List<String> = columns
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

        override fun write(value: T): List<Any> =
            ctorParamNames.map { paramName ->
                value.javaClass.getDeclaredField(paramName).run {
                    isAccessible = true
                    get(value)
                }
            }
    }
}
