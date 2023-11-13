package domain

import kotlinx.serialization.Serializable

@Serializable
data class Table(
    val tableCatalog: String,
    val tableSchema: String,
    val tableName: String,
    val tableType: String,
    val selfReferencingColumnName: String?,
    val referenceGeneration: String?,
    val userDefinedTypeCatalog: String?,
    val userDefinedTypeSchema: String?,
    val userDefinedTypeName: String?,
    val isInsertableInto: Boolean,
    val isTyped: Boolean,
    val commitAction: String?
)
