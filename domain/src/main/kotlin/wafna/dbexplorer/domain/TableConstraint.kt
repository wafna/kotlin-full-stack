package wafna.dbexplorer.domain

data class TableConstraint(
    val constraintCatalog: String,
    val constraintSchema: String,
    val constraintName: String,
    val tableCatalog: String,
    val tableSchema: String,
    val tableName: String,
    val constraintType: String,
    val isDeferrable: Boolean,
    val initiallyDeferred: Boolean,
    val enforced: Boolean,
    val nullsDistinct: Boolean
)
