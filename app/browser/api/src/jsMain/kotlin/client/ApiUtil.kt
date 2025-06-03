package client

import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlinx.serialization.json.Json
import util.makeURL
import web.file.File
import web.http.Headers
import web.http.RequestCache
import web.http.RequestCredentials
import web.http.RequestInit
import web.http.RequestMethod
import web.http.RequestMethod.Companion.GET
import web.http.RequestMethod.Companion.POST
import web.http.RequestMode
import web.http.Response
import web.http.fetch

fun RequestMethod.api(body: dynamic = null): RequestInit =
    RequestInit(
        method = this,
        headers =
            Headers().apply {
                append("Content-Type", "application/json")
                append("Accept", "application/json")
                append("pragma", "no-cache")
            },
        body = body,
        credentials = RequestCredentials.sameOrigin,
        cache = RequestCache.noCache,
        mode = RequestMode.cors,
    )

// nb This would be private but for the inline reified usage, below.
suspend fun RequestInit.call(url: String): Result<Response> {
    val rq: RequestInit = this
    try {
        return success(fetch(url, rq))
    } catch (e: Throwable) {
        val msg = "ERROR: Operation failed: $url"
        console.error(msg, e)
        return failure(Exception(msg, e))
    }
}

/** Asserts an acceptable response status. */
fun Result<Response>.checkStatus(
    status: Int,
    vararg statuses: Int,
): Result<Response> =
    map { response ->
        val code = response.status.toInt()
        if (code == status || statuses.contains(code)) return success(response)
        val msg = "Unexpected status: $code from ${response.url}"
        console.error(msg)
        return failure(Exception(msg))
    }

// Kotlin/JS mangles the field names, presumably to match ktor serialization on the other side,
// which we're not using.  This sucks.
fun String.cleanMangling(): String = replace("_1\":", "\":")

inline fun <reified T> toJson(body: T) = Json.encodeToString(body)

suspend fun get(url: String): Result<Response> = GET.api().call(url).checkStatus(200)

suspend inline fun <reified T> postJson(
    url: String,
    body: T,
): Result<Response> = POST.api(toJson<T>(body)).call(url).checkStatus(200)

suspend fun postFile(
    url: String,
    file: File,
): Result<Response> = POST.api(file).call(url).checkStatus(200)

suspend inline fun <reified T> Result<Response>.json(): Result<T> = map { response ->
    val body = response.text()
    Json.decodeFromString<T>(body.cleanMangling()) ?: throw Error("No data.")
}

/** Constructs URLs for API calls. */
open class ApiSegment(private val baseUrl: String, private val segmentPath: String) {
    fun segmentUrl(
        callPath: String,
        vararg params: Pair<String, String>,
    ): String = makeURL(baseUrl, "/$segmentPath/$callPath", *params)
}

val Result<*>.unit: Result<Unit>
    get() = map {}

