package wafna.fullstack.server.views

import wafna.fullstack.domain.Column
import wafna.fullstack.domain.ForeignKey
import wafna.fullstack.domain.Index
import wafna.fullstack.domain.Table
import wafna.fullstack.domain.TableConstraint

data class TableView(
    val table: Table,
    val columns: List<Column>,
    val tableConstraints: List<TableConstraint>,
    val foreignKeys: List<ForeignKey>,
    val foreignKeyRefs: List<ForeignKey>,
    val indexes: List<Index>
)
