package wafna.fullstack.kdbc

import arrow.core.raise.result
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource
import kotlinx.coroutines.CancellationException
import wafna.fullstack.db.dao.DataBlocksDao
import wafna.fullstack.db.dao.DataRecordsDao
import wafna.fullstack.db.dao.UsersDao
import wafna.fullstack.db.dao.dataBlocksDao
import wafna.fullstack.db.dao.dataRecordsDao
import wafna.fullstack.db.dao.usersDao
import java.sql.Connection

interface AppDb {
    val users: UsersDao
    val dataBlocks: DataBlocksDao
    val dataRecords: DataRecordsDao

    suspend fun <T> transact(borrow: suspend context(Connection) () -> T): Result<T>
}

/** Ignores CancellationException. */
suspend fun <T> cancellableResult(block: suspend () -> T): Result<T> =
    result {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            raise(e)
        }
    }

fun appDb(dataSource: DataSource): AppDb = object : AppDb {
    override val users: UsersDao = usersDao()
    override val dataBlocks: DataBlocksDao = dataBlocksDao()
    override val dataRecords: DataRecordsDao = dataRecordsDao()
    override suspend fun <T> transact(borrow: suspend context(Connection) () -> T): Result<T> =
        cancellableResult { dataSource.withTransaction { borrow() } }
}
