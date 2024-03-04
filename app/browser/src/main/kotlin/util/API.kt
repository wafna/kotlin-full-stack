package util

import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response

private suspend fun fetch(method: String, url: String, body: dynamic = null): Response {
    return window.fetch(
        url, RequestInit(
            method = method,
            body = body,
            headers = kotlin.js.json(
                "Content-Type" to "application/json",
                "Accept" to "application/json",
                "pragma" to "no-cache"
            )
        )
    ).await().apply {
        status.toInt().also {
            check(200 == it || 0 == it) {
                "Operation failed: $status $url".also { msg ->
                    console.log(msg)
                    window.alert(msg)
                }
            }
        }
    }
}

// Verbiage: expressing the semantics of each method.

suspend fun get(url: String): Response =
    fetch("GET", url)

suspend fun put(url: String, body: dynamic): Response =
    fetch("PUT", url, JSON.stringify(body))

suspend fun post(url: String, body: dynamic): Response =
    fetch("POST", url, JSON.stringify(body))

suspend fun delete(url: String): Response =
    fetch("DELETE", url)

suspend inline fun <reified T> json(response: Response): T =
    Json.decodeFromString(response.text().await())


