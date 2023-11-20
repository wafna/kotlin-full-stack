package pages

import API
import domain.*
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
            Columns {
                columns = tableDetail!!.columns
            }
            h.h4 { +"Indexes" }
            Indexes {
                indexes = tableDetail!!.indexes
            }
            h.h4 { +"Constraints" }
            TableConstraints {
                tableConstraints = tableDetail!!.tableConstraints
            }
            h.h4 { +"Foreign Keys" }
            ForeignKeys {
                foreignKeys = tableDetail!!.foreignKeys
            }
            h.h4 { +"References" }
            ForeignKeys {
                foreignKeys = tableDetail!!.foreignKeyRefs
            }
        }
    }
}

private external interface ColumnsProps : Props {
    var columns: List<Column>
}

private val Columns = FC<ColumnsProps> { props ->
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
            props.columns.forEach { column ->
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
}

private external interface ForeignKeysProps : Props {
    var foreignKeys: List<ForeignKey>
}

private val ForeignKeys = FC<ForeignKeysProps> { props ->
    h.table {
        className = ClassName("table table-lg")
        h.thead {
            h.tr {
                h.th { h.span { +"Name" } }
                h.th { h.span { +"Column" } }
                h.th { h.span { +"Target" } }
            }
        }
        h.tbody {
            props.foreignKeys.forEach { fk ->
                h.tr {
                    h.td { h.pre { +fk.constraintName } }
                    h.td {
                        h.a {
                            href = TableDetailRoute.makeHash(fk.schemaName, fk.tableName).href
                            h.pre { +"${fk.schemaName}.${fk.tableName}.${fk.columnName}" }
                        }
                    }
                    h.td {
                        h.a {
                            href = TableDetailRoute.makeHash(fk.foreignSchemaName, fk.foreignTableName).href
                            h.pre { +"${fk.foreignSchemaName}.${fk.foreignTableName}.${fk.foreignColumnName}" }
                        }
                    }
                }
            }
        }
    }
}

private external interface TableConstraintsProps : Props {
    var tableConstraints: List<Constraint>
}

private val TableConstraints = FC<TableConstraintsProps> {props ->
    h.table {
        className = ClassName("table table-lg")
        h.thead {
            h.tr {
                h.th { h.span { +"Name" } }
                h.th { h.span { +"Type" } }
            }
        }
        h.tbody {
            props.tableConstraints.forEach { column ->
                h.tr {
                    h.td { h.pre { +column.constraintName } }
                    h.td { h.pre { +column.constraintType } }
                }
            }
        }
    }
}

private external interface IndexesProps : Props {
    var indexes: List<Index>
}

private val Indexes = FC<IndexesProps> {props ->
    h.table {
        className = ClassName("table table-lg")
        h.thead {
            h.tr {
                h.th { h.span { +"Name" } }
                h.th { h.span { +"Definition" } }
            }
        }
        h.tbody {
            props.indexes.forEach { index ->
                h.tr {
                    h.td { h.pre { +index.indexName } }
                    h.td { h.pre { +index.indexDef } }
                }
            }
        }
    }
}