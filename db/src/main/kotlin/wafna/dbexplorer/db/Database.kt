package wafna.dbexplorer.db

import arrow.core.left
import arrow.core.right
import wafna.dbexplorer.domain.errors.DomainError
import wafna.dbexplorer.domain.errors.DomainResult
import wafna.dbexplorer.util.LazyLogger
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.concurrent.CancellationException
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

context(Database)
suspend fun <T> LazyLogger.selectLogged(
    sql: String,
    vararg params: Any,
    reader: suspend (ResultSet) -> T
): List<T> {
    debug { "SQL\n$sql" }
    return select(sql, *params) { reader(it) }
}

suspend fun <T> Connection.withStatement(sql: String, borrow: suspend PreparedStatement.() -> T) =
    prepareStatement(sql).use { it.borrow() }

suspend fun <R> PreparedStatement.readRecords(reader: suspend (ResultSet) -> R): List<R> = buildList {
    val rs = executeQuery()
    while (rs.next()) {
        add(reader(rs))
    }
}

/**
 * Guards against throw exceptions and translates them to domain errors.
 */
internal suspend fun <T> trap(block: suspend () -> T): DomainResult<T> =
    try {
        block().right()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        DomainError.InternalServerError(e).left()
    }
