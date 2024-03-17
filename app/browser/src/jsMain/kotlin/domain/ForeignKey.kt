package domain

import kotlinx.serialization.Serializable

@Serializable
data class ForeignKey(
    val schemaName: String,
    val tableName: String,
    val constraintName: String,
    val columnName: String,
    val foreignSchemaName: String,
    val foreignTableName: String,
    val foreignColumnName: String,
)
