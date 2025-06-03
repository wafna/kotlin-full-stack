package domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val createdAt: Long,
    val deletedAt: Long?
)
