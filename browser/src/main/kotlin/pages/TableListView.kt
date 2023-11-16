package pages

import API
import TableDetailRoute
import domain.Table
import gridley.DisplayColumnPre
import gridley.DisplayColumnStdHdr
import gridley.createGrid
import kotlinx.coroutines.launch
import mainScope
import react.FC
import react.Props
import react.useEffectOnce
import react.useState
import util.Loading
import react.dom.html.ReactHTML as h

external interface TableListProps : Props {
    var schemaName: String
}

val TableListView = FC<TableListProps> { props ->
    var tables: List<Table>? by useState(null)

    suspend fun updateList() {
        tables = API.listTables(props.schemaName)
    }

    useEffectOnce {
        mainScope.launch {
            updateList()
        }
    }

    when (tables) {
        null -> Loading
        else ->
            (createGrid<Table>()) {
                recordSet = tables!!
                pageSize = 15
                columns = listOf(
                    object : DisplayColumnStdHdr<Table>("Catalog") {
                        override val searchFunction: ((Table) -> String) = { it.tableCatalog.lowercase() }
                        override fun renderField(record: Table): FC<Props> = FC { h.span { +record.tableCatalog } }
                    },
                    object : DisplayColumnPre<Table>("Schema") {
                        override fun value(record: Table): String = record.tableSchema
                    },
                    object : DisplayColumnPre<Table>("Table") {
                        override fun value(record: Table): String = record.tableName
                        override fun renderField(record: Table): FC<Props> = FC {
                            h.a {
                                href = TableDetailRoute.makeHash(record.tableSchema, record.tableName).href
                                h.pre { +record.tableName }
                            }
                        }
                    },
                    object : DisplayColumnPre<Table>("Type") {
                        override fun value(record: Table): String = record.tableType
                    },
                )
            }
    }
}
