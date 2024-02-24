package wafna.dbexplorer.server.routes

import arrow.core.raise.either
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import wafna.dbexplorer.server.ServerContext
import wafna.dbexplorer.server.views.TableView

context(ServerContext)
internal fun Route.api() {
    get("/overview") {
        call.bracket {
            either {
                val schemas = db.meta.listSchemas().bind()
                respond(schemas)
            }
        }
    }
    get("/schema/{schema}") {
        val schemaName = call.parameters["schema"]!!
        call.bracket {
            either {
                val tables = db.meta.listTables(schemaName).bind()
                respond(tables)
            }
        }
    }
    get("/table/{schema}/{table}") {
        val schemaName = call.parameters["schema"]!!
        val tableName = call.parameters["table"]!!
        call.bracket {
            either {
                val table = db.meta.getTable(schemaName, tableName).bind()
                if (null == table) {
                    notFound()
                } else {
                    val columns = db.meta.listColumns(schemaName, tableName).bind()
                    val tableConstraints = db.meta.listTableConstraints(schemaName, tableName).bind()
                        .filter { !it.constraintName.contains("_not_null") }
                    val foreignKeys = db.meta.listForeignKeys(schemaName, tableName).bind()
                    val foreignKeyRefs = db.meta.listForeignKeyRefs(schemaName, tableName).bind()
                    val indexes = db.meta.listIndexes(schemaName, tableName).bind()
                    respond(TableView(table, columns, tableConstraints, foreignKeys, foreignKeyRefs, indexes))
                }
            }
        }
    }
}
