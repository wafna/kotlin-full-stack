package domain

import kotlinx.serialization.Serializable

@Serializable
data class DataBlock(
    val id: String,
    val owner: String,
    val name: String,
    val createdAt: Long,
    val deletedAt: Long?
)
