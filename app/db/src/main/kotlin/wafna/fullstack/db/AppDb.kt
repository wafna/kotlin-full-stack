package wafna.fullstack.kdbc

import wafna.fullstack.db.dao.*
import javax.sql.DataSource

interface AppDb : Database {
    val users: UsersDao
    val dataBlocks: DataBlocksDao
    val dataRecords: DataRecordsDao
}

fun appDb(dataSource: DataSource): AppDb =
    object : AppDb, DatabaseImpl(dataSource) {
        override val users: UsersDao = usersDao()
        override val dataBlocks: DataBlocksDao = dataBlocksDao()
        override val dataRecords: DataRecordsDao = dataRecordsDao()
    }
