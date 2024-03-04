package wafna.fullstack.server

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import wafna.fullstack.util.LazyLogger
import kotlin.system.measureTimeMillis

private object Access

private val accessLog = LazyLogger(Access::class)

/**
 * Wrap this around a route to get a look at the activity on that route.
 */
fun Route.accessLog(callback: Route.() -> Unit): Route {
    val accessLogRoute = createChild(object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    })
    accessLogRoute.intercept(ApplicationCallPipeline.Plugins) {
        val elapsed = measureTimeMillis { proceed() }
        accessLog.info { "$elapsed ms ${call.request.httpMethod.value} ${call.request.uri}" }
    }
    callback(accessLogRoute)
    return accessLogRoute
}
