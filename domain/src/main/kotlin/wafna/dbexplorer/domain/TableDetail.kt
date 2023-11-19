package wafna.dbexplorer.domain

data class TableDetail(
    val table: Table,
    val columns: List<Column>,
    val tableConstraints: List<TableConstraint>,
    val foreignKeys: List<ForeignKey>,
    val foreignKeyRefs: List<ForeignKey>,
    val indexes: List<Index>
)
