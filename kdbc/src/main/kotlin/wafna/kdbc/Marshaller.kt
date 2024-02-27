package wafna.kdbc

import java.sql.ResultSet

/**
 * Creates projections on fields and reads records from a result set.
 */
abstract class Marshaller<T> {
    abstract val columnNames: List<String>
    abstract fun read(resultSet: ResultSet): T
    fun project(prefix: String? = null): String = when {
        prefix.isNullOrBlank() -> columnNames
        else -> columnNames.map { "$prefix.$it" }
    }.joinToString(", ")

    fun inList(): String = "(${List(columnNames.size) { "?" }.joinToString(", ")})"
}
