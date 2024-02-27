package wafna.kdbc

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

class Database(private val dataSource: DataSource) {
    suspend fun <T> withConnection(borrow: suspend Connection.() -> T): T =
        dataSource.connection.use { connection ->
            connection.autoCommit = false
            connection.beginRequest()
            try {
                return connection.borrow().also {
                    connection.commit()
                }
            } catch (e: Throwable) {
                connection.rollback()
                throw e
            } finally {
                connection.endRequest()
            }
        }
}

suspend fun <T> Connection.selectRaw(projection: Projection<T>, sql: String, vararg params: Any): List<T> =
    withStatement(sql) {
        params.forEachIndexed { index, param ->
            setObject(index + 1, param)
        }
        readRecords { projection.read(it) }
    }

suspend fun <T> Connection.select(projection: Projection<T>, alias: String, sql: String, vararg params: Any): List<T> =
    withStatement(
        """SELECT ${projection.alias(alias)}
            |FROM ${projection.tableName}${if (alias.isBlank()) "" else " AS $alias"}
            |$sql""".trimMargin()
    ) {
        params.forEachIndexed { index, param ->
            setObject(index + 1, param)
        }
        readRecords { projection.read(it) }
    }

suspend fun <T> Connection.insert(projection: Projection<T>, records: List<T>): List<Int> =
    withStatement("INSERT INTO ${projection.tableName} (${projection.alias()}) VALUES ${projection.inList()}") {
        records.forEach { record ->
            val values = projection.write(record)
            values.forEachIndexed { index, value ->
                setObject(index + 1, value)
            }
            addBatch()
        }
        executeBatch().toList()
    }

suspend fun Connection.update(sql: String, vararg params: Any): Int =
    withStatement(sql) {
        params.forEachIndexed { index, param ->
            setObject(index + 1, param)
        }
        executeUpdate()
    }

private suspend fun <T> Connection.withStatement(sql: String, borrow: suspend PreparedStatement.() -> T) =
    prepareStatement(sql).use { it.borrow() }

private suspend fun <R> PreparedStatement.readRecords(reader: suspend (ResultSet) -> R): List<R> = buildList {
    val resultSet = executeQuery()
    while (resultSet.next()) {
        add(reader(resultSet))
    }
}
