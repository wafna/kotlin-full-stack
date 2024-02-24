package wafna.database

import java.sql.ResultSet

/**
 * Uses reflection to create a marshaller for a generic type.
 * This type is restricted to Kotlin classes.
 */
inline fun <reified T : Any> genericMarshaller(): Marshaller<T> {
    // Cached type info.
    val theFields = T::class.java.declaredFields.map { it.name }
    val constructor = T::class.constructors.first()
    return object : Marshaller<T>() {
        override val fields: List<String> = theFields

        override fun read(resultSet: ResultSet): T {
            val values = fields.map { fieldName -> resultSet.getObject(fieldName) }
            return constructor.call(*values.toTypedArray())
        }
    }
}
