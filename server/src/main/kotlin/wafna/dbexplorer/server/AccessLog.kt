package wafna.dbexplorer.server

import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext
import wafna.dbexplorer.util.LazyLogger

private object Access

private val accessLog = LazyLogger(Access::class)

/**
 * Wrap this around a route to get a look at the activity on that route.
 */
fun Route.accessLog(callback: Route.() -> Unit): Route =
    createChild(object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation =
            RouteSelectorEvaluation.Constant
    }).also { accessLogRoute ->
        accessLogRoute.intercept(ApplicationCallPipeline.Plugins) {
            accessLog.info { "${call.request.httpMethod.value} ${call.request.uri}" }
            proceed()
        }
        callback(accessLogRoute)
    }
