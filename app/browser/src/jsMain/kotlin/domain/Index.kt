package domain

import kotlinx.serialization.Serializable

@Serializable
data class Index(
    val schemaName: String,
    val tableName: String,
    val indexName: String,
    val tableSpace: String?,
    val indexDef: String
)
