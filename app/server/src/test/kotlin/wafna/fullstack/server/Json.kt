package wafna.fullstack.server

import com.google.gson.Gson
import io.ktor.client.statement.*

val json = Gson()

inline fun <reified T> String.fromJson(): T = json.fromJson(this, T::class.java)

suspend inline fun <reified T> HttpResponse.bodyAs(): T = bodyAsText().fromJson<T>()
