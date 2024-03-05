package wafna.fullstack.domain.errors

import arrow.core.Either

typealias DomainResult<T> = Either<DomainError, T>

sealed class DomainError {
    data class BadRequest(val message: String) : DomainError()
    data class NotFound(val message: String, val exception: Throwable) : DomainError() {
        constructor(message: String) : this(message, RuntimeException())
    }
    data class InternalServerError(val message: String, val exception: Throwable) : DomainError() {
        constructor(message: String) : this(message, RuntimeException(message))
    }
}
