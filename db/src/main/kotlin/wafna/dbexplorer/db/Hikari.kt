package wafna.dbexplorer.db

import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

/**
 * Adapter for HikariCP.
 */
fun HikariDataSource.toConnectionMgr(): ConnectionMgr =
    object : ConnectionMgr {
        override fun <T> borrow(block: (Connection) -> T): T = connection.use(block)
    }
