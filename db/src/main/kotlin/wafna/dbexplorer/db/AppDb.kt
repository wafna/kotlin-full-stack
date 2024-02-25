@file:Suppress("import-ordering")

package wafna.dbexplorer.db

import arrow.core.left
import arrow.core.right
import java.util.concurrent.CancellationException
import javax.sql.DataSource
import wafna.database.Database
import wafna.dbexplorer.domain.errors.DomainError
import wafna.dbexplorer.domain.errors.DomainResult

interface AppDb {
    val meta: MetaDao
}

fun appDb(dataSource: DataSource): AppDb =
    with(Database(dataSource)) {
        object : AppDb {
            override val meta: MetaDao = metaDAO()
        }
    }

/**
 * Guards against thrown exceptions and translates them to domain errors.
 */
internal suspend fun <T> domainResult(block: suspend () -> T): DomainResult<T> =
    try {
        block().right()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        DomainError.InternalServerError(e).left()
    }
