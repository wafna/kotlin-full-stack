package wafna.dbexplorer.domain

data class Index(
    val schemaName: String,
    val tableName: String,
    val indexName: String,
    val tableSpace: String?,
    val indexDef: String
)
