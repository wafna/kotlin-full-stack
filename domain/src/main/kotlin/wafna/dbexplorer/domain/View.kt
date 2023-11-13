package wafna.dbexplorer.domain

data class View(
    val tableCatalog: String,
    val tableSchema: String,
    val tableName: String,
    val viewDefinition: String,
    val checkOption: String?,
    val isUpdatable: Boolean,
    val isInsertableInto: Boolean,
    val isTriggerUpdatable: Boolean,
    val isTriggerDeletable: Boolean,
    val isTriggerInsertableInto: Boolean
)
