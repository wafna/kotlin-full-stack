package wafna.fullstack.server.routes

import arrow.core.Either
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import wafna.fullstack.util.LazyLogger

private object Routes

private val log = LazyLogger<Routes>()

/** Get mandatory request parameter. */
fun ApplicationCall.requireParameter(name: String): String =
    parameters[name] ?: httpError(HttpStatusCode.BadRequest, "Parameter $name is empty.")

/** Get mandatory request parameter with conversion. */
inline fun <reified T> ApplicationCall.requireParameter(
    name: String,
    converter: (String) -> T,
): T =
    runCatching { converter(requireParameter(name)) }
        .getOrElse { e -> httpError(HttpStatusCode.BadRequest, "Parameter $name must be ${T::class.simpleName}.") }

/** Get mandatory Int request parameter. */
internal fun ApplicationCall.requireParameterInt(name: String): Int =
    requireParameter(name) { it.toInt() }

/** Route with error bracket. */
internal fun Route.fget(
    path: String,
    body: suspend ApplicationCall.() -> Unit
) = get(path) { call.bracketBody { body() } }

/** Route with error bracket. */
internal fun Route.fpost(
    path: String,
    body: suspend ApplicationCall.() -> Unit
) = post(path) { call.bracketBody { body() } }

internal suspend fun ApplicationCall.bracketBody(body: suspend ApplicationCall.() -> Unit) =
    Either.catch {
        body()
    }.onLeft { e ->
        log.error(e) { "Internal Error: ${request.httpMethod.value} ${request.uri}" }
        when (e) {
            is HttpException ->
                response.status(e.status)

            else ->
                response.status(HttpStatusCode.InternalServerError)

        }
    }

internal suspend fun ApplicationCall.respondJson(json: String?) {
    response.headers.append("Content-Type", "application/json; charset=utf-8")
    json?.let { respondText(it) } ?: httpError(HttpStatusCode.InternalServerError, "Empty response.")
}

