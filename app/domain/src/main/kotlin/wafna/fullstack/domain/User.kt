package wafna.fullstack.domain

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class UserWip(
    val username: String
) : Wip<User> {
    @OptIn(ExperimentalTime::class)
    override fun reify(createdAt: Instant) = User(entityId(), username, createdAt, null)
}

data class User @OptIn(ExperimentalTime::class) constructor(
    override val id: EID,
    val username: String,
    val createdAt: Instant,
    val deletedAt: Instant?
) : WithEID
