package pages

import API
import domain.TableDetail
import kotlinx.coroutines.launch
import mainScope
import react.FC
import react.Props
import react.useEffectOnce
import react.useState
import util.Loading
import web.cssom.ClassName
import react.dom.html.ReactHTML as h

external interface TableDetailProps : Props {
    var schemaName: String
    var tableName: String
}

val TableDetailView = FC<TableDetailProps> { props ->
    var tableDetail: TableDetail? by useState(null)

    suspend fun updateList() {
        tableDetail = API.tableDetail(props.schemaName, props.tableName)
    }

    useEffectOnce {
        mainScope.launch {
            updateList()
        }
    }

    when (tableDetail) {
        null -> Loading
        else -> {
            h.h3 { +"Table Detail" }
            h.pre { +"${tableDetail!!.table.tableSchema}.${tableDetail!!.table.tableName}" }
            h.h4 { +"Columns" }
            h.table {
                className = ClassName("table table-sm")
                h.thead {
                    h.tr {
                        h.th { h.span { +"Ordinal" } }
                        h.th { h.span { +"Name" } }
                        h.th { h.span { +"Type" } }
                        h.th { h.span { +"Nullable" } }
                        h.th { h.span { +"Default" } }
                    }
                }
                h.tbody {
                    tableDetail!!.columns.forEach { column ->
                        h.tr {
                            h.td { h.pre { +column.ordinalPosition.toString() } }
                            h.td { h.pre { +column.columnName } }
                            h.td { h.pre { +column.dataType } }
                            h.td { h.pre { +column.isNullable.toString() } }
                            h.td { h.pre { +column.columnDefault.toString() } }
                        }
                    }
                }
            }
            h.h4 { +"Constraints" }
            h.table {
                className = ClassName("table table-sm")
                h.thead {
                    h.tr {
                        h.th { h.span { +"Name" } }
                        h.th { h.span { +"Type" } }
                    }
                }
                h.tbody {
                    tableDetail!!.constraints.forEach { column ->
                        h.tr {
                            h.td { h.pre { +column.constraintName } }
                            h.td { h.pre { +column.constraintType } }
                        }
                    }
                }
            }
        }
    }
}
