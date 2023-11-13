package wafna.dbexplorer.db

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

class Database(private val dataSource: DataSource) {
    suspend fun <T> connectionLet(borrow: suspend (Connection) -> T) =
        dataSource.connection.use { borrow(it) }

    suspend fun <T> connectionApply(borrow: suspend Connection.() -> T) =
        dataSource.connection.use { it.borrow() }

    suspend fun <T> select(sql: String, vararg params: Any, reader: suspend (ResultSet) -> T): List<T> =
        connectionApply {
            statementApply(sql) {
                params.forEachIndexed { index, param ->
                    setObject(index + 1, param)
                }
                readRecords { reader(it) }
            }
        }
}

suspend fun <T> Connection.statementLet(sql: String, borrow: suspend (PreparedStatement) -> T) =
    prepareStatement(sql).use { borrow(it) }

suspend fun <T> Connection.statementApply(sql: String, borrow: suspend PreparedStatement.() -> T) =
    prepareStatement(sql).use { it.borrow() }

suspend fun <T> PreparedStatement.readRecords(reader: suspend (ResultSet) -> T): List<T> = buildList {
    val rs = executeQuery()
    while (rs.next()) {
        add(reader(rs))
    }
}
