package wafna.fullstack.server.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import wafna.fullstack.api.API
import wafna.fullstack.domain.toEntityId
import wafna.fullstack.server.UserSession
import wafna.fullstack.server.deleteCookie
import wafna.fullstack.server.domain.AuthResult
import wafna.fullstack.server.setCookie

internal fun Route.sessionRoutes(api: API) {
    fget("/login") {
        val username = requireParameter("username")
        val user = api.users.byUsername(username).internalServerError()?.also {
            setCookie(it)
        }
//            ?: httpError(HttpStatusCode.Unauthorized, "Invalid credentials")
        respondJson(AuthResult(user).toJson())
    }

    fget("/whoami") {
        when (val actorId = sessions.get<UserSession>()?.id?.toEntityId()) {
            null -> {
                deleteCookie()
                respondJson(AuthResult().toJson())
            }

            else -> {
                when (val user = api.users.byId(actorId).internalServerError()) {
                    null -> {
                        deleteCookie()
                        respondJson(AuthResult().toJson())
                    }

                    else -> {
                        respondJson(AuthResult(user).toJson())
                    }
                }
            }
        }
    }

    fget("/logout") {
        deleteCookie()
        respond(HttpStatusCode.OK)
    }
}
