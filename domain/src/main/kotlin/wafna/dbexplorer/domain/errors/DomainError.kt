package wafna.dbexplorer.domain.errors

import arrow.core.Either

typealias DomainResult<T> = Either<DomainError, T>

sealed class DomainError {
    data class BadRequest(val message: String) : DomainError()
    data class NotFound(val message: String, val exception: Throwable) : DomainError()
    data class InternalServerError(val message: String, val exception: Throwable) : DomainError()
}
