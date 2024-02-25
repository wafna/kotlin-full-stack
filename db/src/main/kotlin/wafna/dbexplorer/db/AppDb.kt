@file:Suppress("import-ordering")

package wafna.dbexplorer.db

import arrow.core.left
import arrow.core.right
import java.util.concurrent.CancellationException
import javax.sql.DataSource
import wafna.database.Database
import wafna.dbexplorer.domain.errors.DomainError
import wafna.dbexplorer.domain.errors.DomainResult
import wafna.dbexplorer.util.LazyLogger

interface AppDb {
    val meta: MetaDao
}

fun appDb(dataSource: DataSource): AppDb =
    with(Database(dataSource)) {
        object : AppDb {
            override val meta: MetaDao = metaDAO()
        }
    }
