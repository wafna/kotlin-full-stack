package domain

import kotlinx.serialization.Serializable

@Serializable
data class TableDetail(
    val table: Table,
    val columns: List<Column>,
    val constraints: List<Constraint>,
//    val primaryKey: PrimaryKey?,
//    val foreignKeys: List<ForeignKey>,
//    val indexes: List<Index>
)
