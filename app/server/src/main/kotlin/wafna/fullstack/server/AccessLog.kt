package wafna.fullstack.server

import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.ApplicationSendPipeline
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.round
import wafna.fullstack.util.LazyLogger

private object Access

private val accessLog = LazyLogger<Access>()
private val requestTimes = ConcurrentHashMap<UUID, Long>()

/** Plugin for logging all the routes that get called. */
val AccessPlugin =
    createApplicationPlugin(name = "AccessLog") {
        onCall { call ->
            val requestId = UUID.randomUUID()
            requestTimes[requestId] = System.nanoTime()
            call.response.pipeline.intercept(ApplicationSendPipeline.After) {
                // I'm skeptical of the resulting duration since the status is not set, yet.
                val duration = requestTimes.remove(requestId)?.let { System.nanoTime() - it }
                val durationMs = duration?.let { round(it / 1000.0) / 1000.0 }
                accessLog.info {
                    "${call.request.httpMethod.value} ${call.request.uri} (${durationMs} ms)"
                }
            }
        }
    }
