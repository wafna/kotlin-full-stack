package wafna.fullstack.server.routes

import io.ktor.http.HttpStatusCode

class HttpException(val status: HttpStatusCode, message: String?, cause: Throwable? = null) :
    RuntimeException("[$status] ${if (message.isNullOrBlank()) "<<no message>>" else message}", cause)

fun httpError(status: HttpStatusCode, message: String? = null): Nothing =
    throw HttpException(status, message)

fun <T> Result<T>.bindHttp(status: HttpStatusCode, message: String? = null): T =
    getOrElse { throw HttpException(status, message, it) }

fun <T> Result<T>.internalServerError(message: String? = null): T =
    bindHttp(HttpStatusCode.InternalServerError, message)

fun <T> Result<T>.badRequest(message: String? = null): T = bindHttp(HttpStatusCode.BadRequest, message)