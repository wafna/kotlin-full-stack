package wafna.dbexplorer.server.views

import wafna.dbexplorer.domain.Column
import wafna.dbexplorer.domain.ForeignKey
import wafna.dbexplorer.domain.Index
import wafna.dbexplorer.domain.Table
import wafna.dbexplorer.domain.TableConstraint

data class TableView(
    val table: Table,
    val columns: List<Column>,
    val tableConstraints: List<TableConstraint>,
    val foreignKeys: List<ForeignKey>,
    val foreignKeyRefs: List<ForeignKey>,
    val indexes: List<Index>
)
