package domain

import kotlinx.serialization.Serializable

@Serializable
data class DataRecord(
    val id: String,
    val dataBlockId: String,
    val key: String,
    val data: List<String>,
)
