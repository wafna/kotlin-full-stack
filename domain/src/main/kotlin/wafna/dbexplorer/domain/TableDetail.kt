package wafna.dbexplorer.domain

data class TableDetail(
    val table: Table,
    val columns: List<Column>,
    val constraints: List<Constraint>,
    val indexes: List<Index>
)
