package domain

import kotlinx.serialization.Serializable

@Serializable
data class TableDetail(
    val table: Table,
    val columns: List<Column>,
    val tableConstraints: List<Constraint>,
    val foreignKeys: List<ForeignKey>,
    val foreignKeyRefs: List<ForeignKey>,
    val indexes: List<Index>
//    val primaryKey: PrimaryKey?,
//    val foreignKeys: List<ForeignKey>,
//    val indexes: List<Index>
)
