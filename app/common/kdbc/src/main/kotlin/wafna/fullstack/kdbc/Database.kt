package wafna.fullstack.kdbc

import javax.sql.DataSource
import wafna.fullstack.util.LazyLogger
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

private object Database

val log = LazyLogger<Database>()

/**
 * Execute the given block within a transaction. The transaction is committed if the block completes
 * normally and rolled back if it throws an exception.
 */
suspend fun <T> DataSource.withTransaction(borrow: suspend context(Connection) () -> T): T =
    connection.use { connection ->
        connection.autoCommit = false
        connection.beginRequest()
        try {
            context(connection) { borrow() }.also { connection.commit() }
        } catch (e: Throwable) {
            // Ok to catch CancellationExceptions here because we're about to rethrow them.
            connection.rollback()
            throw e
        } finally {
            connection.endRequest()
        }
    }

/** executeQuery() */
context(_: Connection)
suspend fun <T> select(
    sql: String,
    vararg params: Param,
    reader: ResultSet.() -> T,
): T = withStatement(sql) {
    setParams(params)
    executeQuery().use { it.reader() }
}

/**
 * executeBatch()
 * To simplify usage and reduce memory footprint, the records are presented as an Iterator.
 * Callers should produce the param lists on demand.
 */
context(_: Connection)
suspend fun insert(
    sql: String,
    records: Iterator<List<Param>>,
): IntArray = withStatement(sql) {
    records.forEach { record ->
        setParams(record)
        addBatch()
    }
    executeBatch()
}

/** executeUpdate() */
context(cx: Connection)
suspend fun update(
    sql: String,
    vararg params: Param,
): Int = withStatement(sql) {
    setParams(params)
    executeUpdate()
}

/** executeUpdate() */
context(cx: Connection)
suspend fun update(
    sql: String,
    params: Iterable<Param>,
): Int = withStatement(sql) {
    setParams(params)
    executeUpdate()
}

context(cx: Connection)
suspend inline fun <T> withStatement(
    sql: String,
    borrow: suspend PreparedStatement.() -> T,
): T {
    log.debug { "Executing SQL\n```sql\n$sql\n```" }
    return runCatching { cx.prepareStatement(sql).use { it.borrow() } }
        .getOrElse {
            throw RuntimeException("Error while executing SQL\n$sql", it)
        }
}

/**
 * Interpolates the params into the prepared statement in order.
 */
private fun PreparedStatement.setParams(params: Iterable<Param>) =
    params.forEachIndexed { index, param -> param(this, 1 + index) }

/**
 * Interpolates the params into the prepared statement in order.
 */
private fun PreparedStatement.setParams(params: Array<out Param>) =
    params.forEachIndexed { index, param -> param(this, 1 + index) }
