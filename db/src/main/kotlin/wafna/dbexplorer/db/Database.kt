package wafna.dbexplorer.db

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

class Database(private val dataSource: DataSource) {
    suspend fun <T> withConnection(borrow: suspend Connection.() -> T) =
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

suspend fun <T> Connection.withStatement(sql: String, borrow: suspend PreparedStatement.() -> T) =
    prepareStatement(sql).use { it.borrow() }

suspend fun <R> PreparedStatement.readRecords(reader: suspend (ResultSet) -> R): List<R> = buildList {
    val rs = executeQuery()
    while (rs.next()) {
        add(reader(rs))
    }
}
