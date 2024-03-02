package wafna.fullstack.server.routes

import arrow.core.Either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import wafna.fullstack.domain.errors.DomainError
import wafna.fullstack.domain.errors.DomainResult
import wafna.fullstack.util.LazyLogger

private object Routes

private val log = LazyLogger(Routes::class)

/**
 * The last stop for any uncaught exceptions in a route plus a translation of domain errors to HTTP status codes.
 */
internal suspend fun <T> ApplicationCall.bracket(block: suspend ApplicationCall.() -> DomainResult<T>) =
    Either.catch {
        block().onLeft(::domainErrorLogAndRespond)
    }.onLeft { e ->
        log.error(e) { "Internal Error: ${request.httpMethod.value} ${request.uri}" }
        internalServerError()
    }

internal fun ApplicationCall.internalServerError() {
    response.status(HttpStatusCode.InternalServerError)
}

internal fun ApplicationCall.badRequest() {
    response.status(HttpStatusCode.BadRequest)
}

internal fun ApplicationCall.notFound() {
    response.status(HttpStatusCode.NotFound)
}

private fun ApplicationCall.domainErrorLogAndRespond(it: DomainError) {
    when (it) {
        is DomainError.BadRequest -> {
            log.warn { "Bad Request: ${request.httpMethod.value} ${request.uri}" }
            badRequest()
        }

        is DomainError.NotFound -> {
            log.warn(it.exception) { "Not Found: ${request.httpMethod.value} ${request.uri}" }
            notFound()
        }

        is DomainError.InternalServerError -> {
            log.error(it.exception) { "Internal Error: ${request.httpMethod.value} ${request.uri}" }
            internalServerError()
        }
    }
}
