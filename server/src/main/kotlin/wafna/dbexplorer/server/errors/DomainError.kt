package wafna.dbexplorer.server.errors

sealed class DomainError {
    data class BadRequest(val message: String) : DomainError()
    data class NotFound(val message: String, val exception: Throwable) : DomainError()
    data class InternalServerError(val exception: Throwable) : DomainError()
}
