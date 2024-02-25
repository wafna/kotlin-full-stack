@file:Suppress("import-ordering")

package wafna.dbexplorer.db

import wafna.database.Database
import javax.sql.DataSource

interface AppDb {
    val meta: MetaDao
}

fun appDb(dataSource: DataSource): AppDb =
    with(Database(dataSource)) {
        object : AppDb {
            override val meta: MetaDao = metaDAO()
        }
    }
