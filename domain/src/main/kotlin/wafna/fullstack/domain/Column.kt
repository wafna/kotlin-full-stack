package wafna.fullstack.domain

data class Column(
    val tableCatalog: String,
    val tableSchema: String,
    val tableName: String,
    val columnName: String,
    val ordinalPosition: Int,
    val dataType: String,
    val columnDefault: String?,
    val isNullable: Boolean
)
