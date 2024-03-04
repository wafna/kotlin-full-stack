package wafna.kdbc

import arrow.core.Either
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

/**
 * Execute the given block within a transaction.
 * The transaction is committed if the block completes normally and rolled back if it throws an exception.
 */
suspend fun <T> DataSource.transact(
    borrow: suspend Connection.() -> T
): T = connection.use { connection ->
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
suspend fun <T> DataSource.autoCommit(
    borrow: suspend Connection.() -> T
): T = connection.use { connection ->
    connection.autoCommit = true
    connection.beginRequest()
    try {
        return connection.borrow()
    } finally {
        connection.endRequest()
    }
}

fun <T> Connection.selectRecords(
    sql: String
): SelectParamCollector<T> = object : SelectParamCollector<T>() {
    override fun invoke(vararg params: Any?): ResultSetReceiver<T> = object : ResultSetReceiver<T>() {
        override fun read(read: (ResultSet) -> T): List<T> =
            doSelect(sql, params.toList(), read)

        override fun read(reader: RecordReader<T>): List<T> =
            doSelect(sql, params.toList()) { reader.read(it) }
    }
}

private inline fun <T> Connection.doSelect(
    sql: String,
    params: List<Any?>,
    reader: (ResultSet) -> T
): List<T> = withStatement(sql) {
    setParams(params)
    buildList {
        executeQuery().use { resultSet ->
            while (resultSet.next()) {
                val record = reader(resultSet)
                add(record)
            }
        }
    }
}


fun <T> Connection.insertRecords(
    tableName: String,
    fieldNames: List<String>,
    records: List<T>
): BatchReceiver<T> = object : BatchReceiver<T>() {
    override fun write(write: (T) -> List<Any?>) =
        doInsert(tableName, fieldNames, records, write)

    override fun write(writer: BatchWriter<T>): List<Int> =
        doInsert(tableName, fieldNames, records) { writer.write(it) }
}

private inline fun <T> Connection.doInsert(
    tableName: String,
    fieldNames: List<String>,
    records: List<T>,
    writer: (T) -> List<Any?>
): List<Int> = withStatement(
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

fun Connection.updateRecords(
    sql: String
): ParamCollector<Int> = object : ParamCollector<Int>() {
    override fun invoke(vararg params: Any?): Int = withStatement(sql) {
        setParams(params.toList())
        executeUpdate()
    }
}

private inline fun <T> Connection.withStatement(
    sql: String,
    borrow: PreparedStatement.() -> T
) = prepareStatement(sql).use { it.borrow() }

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
