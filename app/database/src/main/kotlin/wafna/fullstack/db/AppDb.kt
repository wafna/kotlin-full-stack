package wafna.fullstack.db

import javax.sql.DataSource

interface AppDb {
    val meta: MetaDao
}

fun appDb(dataSource: DataSource): AppDb =
    object : AppDb {
        override val meta: MetaDao = metaDAO(dataSource)
    }
