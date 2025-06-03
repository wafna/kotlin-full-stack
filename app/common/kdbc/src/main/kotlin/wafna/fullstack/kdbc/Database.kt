package wafna.fullstack.kdbc

import arrow.core.raise.result
import kotlinx.coroutines.CancellationException
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

/** Ignores CancellationException. */
private suspend fun <T> cancellableResult(block: suspend () -> T): Result<T> =
    result {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            raise(RuntimeException(e))
        }
    }

interface Database {
    val dataSource: DataSource
    suspend fun <T> transact(borrow: suspend (Connection) -> T): Result<T>
}

open class DatabaseImpl(override val dataSource: DataSource) : Database {
    override suspend fun <T> transact(borrow: suspend (Connection) -> T): Result<T> =
        cancellableResult { dataSource.withTransaction { borrow(it) } }
}

/**
 * Execute the given block within a transaction. The transaction is committed if the block completes
 * normally and rolled back if it throws an exception.
 */
suspend fun <T> DataSource.withTransaction(borrow: suspend (Connection) -> T): T =
    connection.use { connection ->
        connection.autoCommit = false
        connection.beginRequest()
        try {
            borrow(connection).also { connection.commit() }
        } catch (e: Throwable) {
            // Ok to catch CancellationExceptions here because we're about to rethrow them.
            connection.rollback()
            throw e
        } finally {
            connection.endRequest()
        }
    }

/**
 * Execute the given block within a transaction. The transaction is committed if the block completes
 * normally and rolled back if it throws an exception.
 */
suspend fun <T> DataSource.runTransaction(borrow: suspend Connection.() -> T): T =
    withTransaction { it.borrow() }

/** executeQuery() */
fun <T> Connection.select(
    sql: String,
    vararg params: Param,
    reader: ResultSet.() -> T,
): T =
    onStatement(sql) {
        setParams(params)
        executeQuery().use { it.reader() }
    }

/**
 * executeBatch() In order to simplify usage and not have a full mirrored collection of params, the
 * records are presented as an Iterator. The iterator produces param lists on demand.
 */
fun Connection.insert(
    sql: String,
    records: Iterator<List<Param>>,
): IntArray =
    onStatement(sql) {
        records.forEach { record ->
            setParams(record)
            addBatch()
        }
        executeBatch()
    }

/** executeUpdate() */
fun Connection.update(
    sql: String,
    vararg params: Param,
): Int =
    onStatement(sql) {
        setParams(params)
        executeUpdate()
    }

/** executeUpdate() */
fun Connection.update(
    sql: String,
    params: Iterable<Param>,
): Int =
    onStatement(sql) {
        setParams(params)
        executeUpdate()
    }

@Suppress("SqlSourceToSinkFlow")
inline fun <T> Connection.onStatement(
    sql: String,
    borrow: PreparedStatement.() -> T,
) = prepareStatement(sql).use { it.borrow() }

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
