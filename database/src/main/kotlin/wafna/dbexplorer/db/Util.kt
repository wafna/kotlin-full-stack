package wafna.dbexplorer.db

import arrow.core.left
import arrow.core.right
import java.util.concurrent.CancellationException
import wafna.dbexplorer.domain.errors.DomainError
import wafna.dbexplorer.domain.errors.DomainResult
import wafna.dbexplorer.util.LazyLogger

private val log = LazyLogger(AppDb::class)

/**
 * Terminates thrown exceptions and returns domain errors.
 */
internal suspend fun <T> domainResult(block: suspend () -> T): DomainResult<T> =
    try {
        block().right()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        val message = "Error in domainResult"
        log.error(e) { message }
        DomainError.InternalServerError(message, e).left()
    }
