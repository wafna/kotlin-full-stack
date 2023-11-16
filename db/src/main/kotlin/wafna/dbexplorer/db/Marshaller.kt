package wafna.dbexplorer.db

import java.sql.ResultSet

/**
 * Creates projections on fields and reads records from a [ResultSet].
 */
abstract class Marshaller<T> {
    abstract val fields: List<String>
    abstract fun read(resultSet: ResultSet): T

    operator fun invoke(resultSet: ResultSet): T = read(resultSet)
    operator fun invoke(prefix: String? = null): String = project(prefix)

    fun project(prefix: String? = null): String = when {
        prefix.isNullOrBlank() -> fields
        else -> fields.map { "$prefix.$it" }
    }.joinToString(", ")
}
