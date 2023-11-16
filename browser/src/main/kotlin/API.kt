import domain.Schema
import domain.Table
import domain.TableDetail
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import kotlin.js.json

private suspend fun fetch(method: String, url: String, body: dynamic = null): Response {
    return window.fetch(
        url, RequestInit(
            method = method,
            body = body,
            headers = json(
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

private suspend fun get(url: String): Response =
    fetch("GET", url)

private suspend fun put(url: String, body: dynamic): Response =
    fetch("PUT", url, JSON.stringify(body))

private suspend fun post(url: String, body: dynamic): Response =
    fetch("POST", url, JSON.stringify(body))

private suspend fun delete(url: String): Response =
    fetch("DELETE", url)

private suspend inline fun <reified T> json(response: Response): T =
    Json.decodeFromString(response.text().await())

private const val apiRoot = "http://localhost:8686/api"

private fun makeURL(path: String, vararg params: Pair<String, String>): String = buildString {
    append(apiRoot)
    append("/")
    append(path)
    if (params.isNotEmpty()) {
        append("?")
        var sep = false
        for (param in params) {
            if (sep) append("&") else sep = true
            append(param.first)
            append("=")
            append(param.second)
        }
    }
}

object API {
    suspend fun listSchemas(): List<Schema> =
        json(get(makeURL("schemas")))

    suspend fun listTables(schemaName: String): List<Table> =
        json(get(makeURL("tables/${schemaName}")))

    suspend fun tableDetail(schemaName: String, tableName: String): TableDetail =
        json(get(makeURL("tables/${schemaName}/${tableName}")))
}
