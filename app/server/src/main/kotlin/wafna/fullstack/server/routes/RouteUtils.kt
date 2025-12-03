package wafna.fullstack.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
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

internal fun Route.bracketGet(
    path: String,
    body: suspend ApplicationCall.() -> Unit
) = get(path) { call.bracketResponse { body() } }

internal fun Route.bracketPost(
    path: String,
    body: suspend ApplicationCall.() -> Unit
) = post(path) { call.bracketResponse { body() } }

/**
 * The last stop for any uncaught exceptions in a route.
 * HTTP error status codes are reserved for genuine errors, e.g. bad request, which is a bug.
 * User error messages are handled separately in the browser, hence the OK.
 */
internal suspend fun ApplicationCall.bracketResponse(body: suspend ApplicationCall.() -> Unit) =
    try {
        body()
    } catch (e: Throwable) {
        log.error(e) { "Exception in HTTP chain: ${request.httpMethod.value} ${request.uri}" }
        when (e) {
            is HttpException -> response.status(e.status)
            else -> response.status(HttpStatusCode.InternalServerError)
        }
    }

internal suspend fun ApplicationCall.respondJson(json: String?) {
    response.headers.append("Content-Type", "application/json; charset=utf-8")
    json?.let { respondText(it) } ?: httpError(HttpStatusCode.InternalServerError, "Empty response.")
}

