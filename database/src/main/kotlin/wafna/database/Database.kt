package wafna.database

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

class Database(private val dataSource: DataSource) {
    private suspend fun <T> withConnection(borrow: suspend Connection.() -> T) =
        dataSource.connection.use { it.borrow() }

    suspend fun <T> select(sql: String, vararg params: Any, reader: suspend (ResultSet) -> T): List<T> =
        withConnection {
            withStatement(sql) {
                params.forEachIndexed { index, param ->
                    setObject(index + 1, param)
                }
                readRecords { reader(it) }
            }
        }
}

private suspend fun <T> Connection.withStatement(sql: String, borrow: suspend PreparedStatement.() -> T) =
    prepareStatement(sql).use { it.borrow() }

private suspend fun <R> PreparedStatement.readRecords(reader: suspend (ResultSet) -> R): List<R> = buildList {
    val resultSet = executeQuery()
    while (resultSet.next()) {
        add(reader(resultSet))
    }
}
