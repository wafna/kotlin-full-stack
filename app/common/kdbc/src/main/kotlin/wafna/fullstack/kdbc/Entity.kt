package wafna.fullstack.kdbc

import java.sql.Connection
import java.sql.ResultSet

/** An element of the projection that maps to an object These are used to generate SQL. */
data class Field(val name: String, val sqlType: String? = null) {
    init {
        require(name.isNotEmpty()) { "Name required." }
        require(sqlType?.isEmpty() != true) { "sqlType must be non-empty if provided." }
    }
}

/** Syntactic convenience. */
fun String.field(sqlType: String? = null) = Field(this, sqlType)

/** Syntactic convenience. */
val String.field: Field
    get() = Field(this, null)

/**
 * Supporting class for persisted entities. Writes records to prepared statements. Reads records
 * from result sets. Generates SQL.
 */
abstract class Entity<R>(val table: String, val fields: List<Field>) {
    init {
        val duplicates = fields.groupBy { it.name }.filter { 1 < it.value.size }
        require(duplicates.isEmpty()) {
            "Duplicate fields detected: ${duplicates.keys.joinToString(", ")}."
        }
    }

    fun projection(alias: String): String = fields.joinToString(", ") {
        if (alias.isEmpty()) it.name else "$alias.${it.name}"
    }

    val fieldMap = fields.associateBy { it.name }

    /** Marshal one row of a ResultSet into a record. */
    abstract fun read(resultSet: ResultSet): R

    /**
     * Create a list of parameter setters in the order of the fields from a record. Some types require
     * the connection to instantiate, e.g. java.sql.Array.
     */
    abstract fun write(
        connection: Connection,
        record: R,
    ): List<Param>

    /** Generates the head of a SELECT statement. */
    fun selectHead(alias: String = ""): String =
        "SELECT ${projection(alias)} FROM $table${if (alias.isEmpty()) "" else " $alias"}"

    /**
     * @param connection The transaction holder.
     * @param alias The alias applied to the table in the head.
     * @param tail The SQL following the head, e.g. JOIN and WHERE.
     * @param params The values of the arguments in the SQL in lexical order.
     */
    suspend fun select(
        connection: Connection,
        alias: String,
        tail: String,
        vararg params: Param,
    ): List<R> = connection.select("${selectHead(alias)} $tail", *params) { readRecords(::read) }

    suspend fun select(
        connection: Connection,
        alias: String,
        tail: String,
        params: Collection<Param>,
    ): List<R> =
        connection.select("${selectHead(alias)} $tail", *params.toTypedArray()) {
            readRecords(::read)
        }

    /**
     * Every INSERT has the same form. This variable form is useful for tables with default fields
     * that are not inserted (e.g. deleted_at). This enforces that the fields exist.
     */
    private fun insertHead(fieldNames: Iterable<String>): String =
        "INSERT INTO $table (${fieldNames.joinToString(", ")}) VALUES (${fieldList(namesToFields(fieldNames))})"

    suspend fun insert(
        connection: Connection,
        records: Iterable<R>,
    ): IntArray =
        connection.insert(
            sql = insertHead(fields.map { it.name }),
            records = records.transformer { write(connection, it) },
        )

    suspend fun update(
        connection: Connection,
        fieldNames: Iterable<String>,
        where: String,
        vararg params: Param,
    ): Int =
        connection.update(
            "UPDATE $table SET ${fieldListNamed(namesToFields(fieldNames))} WHERE $where",
            *params,
        )

    suspend fun update(
        connection: Connection,
        fieldNames: Iterable<String>,
        where: String,
        params: Collection<Param>,
    ): Int =
        connection.update(
            "UPDATE $table SET ${fieldListNamed(namesToFields(fieldNames))} WHERE $where",
            params,
        )

    fun namesToFields(names: Iterable<String>): List<Field> =
        names.map { fieldMap[it] ?: error("Unknown field name $it") }

    companion object {
        fun fieldList(fields: Iterable<Field>): String =
            fields.joinToString(", ") {
                when (val sqlType = it.sqlType) {
                    null -> "?"
                    else -> "? :: $sqlType"
                }
            }

        fun fieldListNamed(fields: Iterable<Field>): String =
            fields.joinToString(", ") {
                when (val sqlType = it.sqlType) {
                    null -> "${it.name} = ?"
                    else -> "${it.name} = ? :: $sqlType"
                }
            }
    }
}
