package wafna.database

import java.sql.ResultSet
import kotlin.reflect.jvm.javaType

interface FieldNameConverter {
    fun toColumnName(name: String): String
}

/**
 * Uses reflection to create a marshaller for a generic type.
 * Projection column names are inferred from the constructor parameter names using the case converter.
 * The result set column names are mapped to the constructor parameters using the case converter.
 * This type T is restricted to Kotlin classes.
 */
inline fun <reified T : Any> genericMarshaller(caseConverter: FieldNameConverter): Marshaller<T> {
    // Cached type and mapping info.
    val type = T::class
    val ctor = type.constructors.first()
    val ctorParams = ctor.parameters
        .associateBy { it.name!! }
        .mapValues { it.value.type.javaType }
    val ctorParamNames = ctorParams.keys.toList()
    val columns = ctorParamNames.map { caseConverter.toColumnName(it) }
    val nameMap = ctorParamNames.zip(columns).toMap()

    return object : Marshaller<T>() {
        override val columnNames: List<String> = columns

        override fun read(resultSet: ResultSet): T {
            val values = ctorParamNames.map { paramName ->
                // We need to match on the java type.
                when (ctorParams[paramName]!!) {
                    Int::class.java ->
                        resultSet.getInt(nameMap[paramName]!!)

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
    }
}
