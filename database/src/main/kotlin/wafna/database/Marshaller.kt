package wafna.database

import java.sql.ResultSet

/**
 * Creates projections on fields and reads records from a [ResultSet].
 */
abstract class Marshaller<T> {
    abstract val fields: List<String>
    abstract fun read(resultSet: ResultSet): T
    fun project(prefix: String? = null): String = when {
        prefix.isNullOrBlank() -> fields
        else -> fields.map { "$prefix.$it" }
    }.joinToString(", ")
}
