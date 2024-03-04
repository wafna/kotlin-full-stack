package wafna.kdbc

import arrow.core.Either
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

class Database(private val dataSource: DataSource) {
    /**
     * Execute the given block within a transaction.
     * The transaction is committed if the block completes normally and rolled back if it throws an exception.
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
     * Execute the given block, auto committing on return.
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
fun <T> Connection.selectRecords(sql: String): SelectParamCollector<T> =
    object : SelectParamCollector<T>() {
        override fun invoke(vararg params: Any?): ResultSetReceiver<T> =
            object : ResultSetReceiver<T>() {
                override fun read(read: (ResultSet) -> T): List<T> =
                    doSelect(sql, params.toList(), read)

                override fun read(reader: ResultSetReader<T>): List<T> =
                    doSelect(sql, params.toList()) { reader.read(it) }
            }
    }

private fun <T> Connection.doSelect(sql: String, params: List<Any?>, reader: (ResultSet) -> T): List<T> =
    withStatement(sql) {
        setParams(params)
        buildList {
            executeQuery().use { resultSet ->
                while (resultSet.next()) {
                    add(reader(resultSet))
                }
            }
        }
    }


/**
 * Formulates an INSERT and batches the records.
 */
fun <T> Connection.insertRecords(tableName: String, fieldNames: List<String>, records: List<T>): BatchReceiver<T> =
    object : BatchReceiver<T>() {
        override operator fun invoke(writer: (T) -> List<Any?>) =
            withStatement(
                "INSERT INTO $tableName (${fieldNames.joinToString(", ")}) VALUES (${
                    List(fieldNames.size) { "?" }.joinToString(", ")
                })"
            ) {
                records.forEach { record ->
                    val values = writer(record)
                    setParams(values)
                    addBatch()
                }
                executeBatch().toList()
            }
    }

/**
 * Updates are just free form non-selects.
 */
fun Connection.updateRecords(sql: String): ParamCollector<Int> =
    object : ParamCollector<Int>() {
        override fun invoke(vararg params: Any?): Int {
            return withStatement(sql) {
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

/**
 * Provides the syntax that separates the SQL from the parameters.
 */
abstract class ParamCollector<T> internal constructor() {
    abstract operator fun invoke(vararg params: Any?): T
}

/**
 * Collects parameters with a follow-on reader.
 */
abstract class SelectParamCollector<T> internal constructor() {
    abstract operator fun invoke(vararg params: Any?): ResultSetReceiver<T>
}

abstract class BatchReceiver<T> {
    abstract operator fun invoke(writer: (T) -> List<Any?>): List<Int>
}

abstract class ResultSetReceiver<T> {
    abstract fun read(read: (ResultSet) -> T): List<T>
    abstract fun read(reader: ResultSetReader<T>): List<T>
}

abstract class ResultSetReader<T> {
    abstract fun read(resultSet: ResultSet): T
}
