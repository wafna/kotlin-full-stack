package wafna.fullstack.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.Serializable
import wafna.fullstack.domain.EID
import wafna.fullstack.domain.User
import wafna.fullstack.domain.toEntityId

/** Requires a valid session cookie and extracts its data. */
suspend fun ApplicationCall.withSession(block: suspend (EID) -> Unit) {
    when (val actor = sessions.get<UserSession>()) {
        null -> respond(HttpStatusCode.Unauthorized)
        else -> block(actor.id.toEntityId())
    }
}

internal const val USER_SESSION = "user_session"

internal fun ApplicationCall.setCookie(user: User) =
    sessions.set(UserSession(id = user.id.toString()))


internal fun ApplicationCall.deleteCookie() =
    sessions.clear<UserSession>()

/** The data for a user session. The id is a string to keep serialization simple. */
@Serializable
data class UserSession(val id: String)
