package wafna.kdbc

import arrow.core.Either
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

class Database(private val dataSource: DataSource) {
    /**
     * Execute the given block within a transaction.
     * The transaction is committed if the block completes normally, and rolled back if it throws an exception.
     */
    suspend fun <T> transact(borrow: suspend Connection.() -> T): T =
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            connection.beginRequest()
            try {
                return connection.borrow().also {
                    connection.commit()
                }
            } catch (e: Throwable) {
                // Ok to catch CancellationExceptions here because we're about to rethrow them.
                connection.rollback()
                throw e
            } finally {
                connection.endRequest()
            }
        }

    /**
     * Execute the given block, auto committing along the way.
     */
    suspend fun <T> autoCommit(borrow: suspend Connection.() -> T): T =
        dataSource.connection.use { connection ->
            connection.autoCommit = true
            connection.beginRequest()
            try {
                return connection.borrow()
            } finally {
                connection.endRequest()
            }
        }
}

/**
 * Formulates a SELECT statement with the projection and table at the head.
 */
fun <T> Connection.select(projection: Projection<T>, alias: String, sql: String): ParamCollector<List<T>> =
    object : ParamCollector<List<T>>() {
        override fun invoke(vararg params: Any?): List<T> {
            return withStatement(
                """SELECT ${projection.alias(alias)}
                |FROM ${projection.tableName}${if (alias.isBlank()) "" else " AS $alias"}
                |$sql""".trimMargin()
            ) {
                setParams(params.toList())
                readRecords { projection.read(it) }
            }
        }
    }

fun <T> Connection.selectCustom(projection: Projection<T>, sql: String): ParamCollector<List<T>> =
    object : ParamCollector<List<T>>() {
        override fun invoke(vararg params: Any?): List<T> {
            return withStatement(sql) {
                setParams(params.toList())
                readRecords { projection.read(it) }
            }
        }
    }

/**
 * Formulates an INSERT and batches the records.
 */
fun <T> Connection.insert(projection: Projection<T>, records: List<T>): List<Int> =
    withStatement("INSERT INTO ${projection.tableName} (${projection.alias()}) VALUES ${projection.inList()}") {
        records.forEach { record ->
            val values = projection.write(record)
            setParams(values)
            addBatch()
        }
        executeBatch().toList()
    }

/**
 * Updates are just free form non-selects.
 */
fun Connection.update(sql: String): ParamCollector<Int> =
    object : ParamCollector<Int>() {
        override fun invoke(vararg params: Any?): Int {
            return withStatement(sql) {
                setParams(params.toList())
                executeUpdate()
            }
        }
    }

/**
 * Formulates a DELETE on the table.
 */
fun <T> Connection.delete(projection: Projection<T>, sql: String): ParamCollector<Int> =
    object : ParamCollector<Int>() {
        override fun invoke(vararg params: Any?): Int {
            return withStatement("DELETE FROM ${projection.tableName} WHERE $sql") {
                setParams(params.toList())
                executeUpdate()
            }
        }
    }

private inline fun <T> Connection.withStatement(sql: String, borrow: PreparedStatement.() -> T) =
    prepareStatement(sql).use { it.borrow() }

private fun PreparedStatement.setParams(params: List<Any?>) {
    params.forEachIndexed { index, param ->
        val place = index + 1
        Either.catch {
            if (null == param)
                setNull(place, java.sql.Types.NULL)
            else
                setObject(place, param)
        }.onLeft { e ->
            throw IllegalArgumentException("Error setting parameter $place to $param", e)
        }
    }
}

private fun <R> PreparedStatement.readRecords(reader: (ResultSet) -> R): List<R> = buildList {
    executeQuery().use { resultSet ->
        while (resultSet.next()) {
            add(reader(resultSet))
        }
    }
}

/**
 * Provides the syntax that separates the SQL from the parameters.
 */
abstract class ParamCollector<T> internal constructor() {
    abstract operator fun invoke(vararg params: Any?): T
}

