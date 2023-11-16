package pages

import API
import TablesRoute
import domain.Schema
import gridley.DisplayColumnPre
import gridley.createGrid
import kotlinx.coroutines.launch
import mainScope
import react.FC
import react.Props
import react.useEffectOnce
import react.useState
import util.Loading
import react.dom.html.ReactHTML as h

/**
 * This is the nexus of the grid where all the bits are wired together.
 */
val SchemaListView = FC<Props> {

    var schemas: List<Schema>? by useState(null)

    suspend fun updateList() {
        schemas = API.listSchemas()
    }

    useEffectOnce {
        mainScope.launch {
            updateList()
        }
    }

    when (schemas) {
        null -> Loading
        else ->
            (createGrid<Schema>()) {
                recordSet = schemas!!
                pageSize = 15
                columns = listOf(
                    object : DisplayColumnPre<Schema>("Catalog") {
                        override fun value(record: Schema): String = record.catalogName
                    },
                    object : DisplayColumnPre<Schema>("Schema") {
                        override fun value(record: Schema): String = record.schemaName
                        override fun renderField(record: Schema): FC<Props> = FC {
                            h.a {
                                href = TablesRoute.makeHash(record.schemaName).href
                                h.pre { +record.schemaName }
                            }
                        }
                    },
                    object : DisplayColumnPre<Schema>("Owner") {
                        override fun value(record: Schema): String = record.schemaOwner
                    },
                )
            }
    }
}
