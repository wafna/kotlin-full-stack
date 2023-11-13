package wafna.dbexplorer.db

import java.sql.Connection

/**
 * This abstracts the database connection pool implementation.
 */
interface ConnectionMgr {
    fun <T> borrow(block: (Connection) -> T): T
}
