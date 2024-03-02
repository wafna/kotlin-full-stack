package wafna.fullstack.db

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.util.concurrent.CancellationException
import wafna.fullstack.domain.errors.DomainError
import wafna.fullstack.domain.errors.DomainResult
import wafna.fullstack.util.LazyLogger

private val log = LazyLogger(AppDb::class)

/**
 * Terminates thrown exceptions and returns domain errors.
 */
internal suspend fun <T> domainResult(block: suspend () -> T): DomainResult<T> = Either
    .catch { block() }
    .mapLeft { e ->
        val message = "Error in domainResult"
        log.error(e) { message }
        DomainError.InternalServerError(message, e)
    }
