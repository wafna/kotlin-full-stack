package wafna.fullstack.domain

import kotlinx.datetime.Instant

data class UserWip(
    val username: String
) : Wip<User> {
    override fun reify(createdAt: Instant) = User(entityId(), username, createdAt, null)
}

data class User(
    override val id: EID,
    val username: String,
    val createdAt: Instant,
    val deletedAt: Instant?
) : WithEID
