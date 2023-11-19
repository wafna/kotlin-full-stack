package wafna.dbexplorer.server

import arrow.core.Either
import arrow.core.raise.either
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import wafna.dbexplorer.domain.TableDetail
import wafna.dbexplorer.util.LazyLogger

fun ApplicationCall.ok() = response.status(HttpStatusCode.OK)
fun ApplicationCall.internalServerError() = response.status(HttpStatusCode.InternalServerError)
fun ApplicationCall.notFound() = response.status(HttpStatusCode.NotFound)

private object API

private val log = LazyLogger(API::class)

suspend fun ApplicationCall.bracket(
    status: HttpStatusCode = HttpStatusCode.OK,
    block: suspend ApplicationCall.() -> Unit
) = either {
    Either.catch { block() }.bind()
    response.status(status)
}.mapLeft { e ->
    log.error(e) { "HTTP Error: ${request.httpMethod.value} ${request.uri}" }
    internalServerError()
}

context(ServerContext)
internal fun Route.api() {
    get("/schemas") {
        call.bracket {
            val schemas = db.meta.listSchemas()
            respond(schemas)
        }
    }
    route("/tables"){
        get("/{schema}") {
            val schemaName = call.parameters["schema"]!!
            call.bracket {
                val tables = db.meta.listTables(schemaName)
                respond(tables)
                ok()
            }
        }
        get("/{schema}/{table}") {
            val schemaName = call.parameters["schema"]!!
            val tableName = call.parameters["table"]!!
            call.bracket {
                val table = db.meta.getTable(schemaName, tableName)
                if (null == table) {
                    notFound()
                    return@bracket
                }
                val columns = db.meta.listColumns(schemaName, tableName)
                val tableConstraints = db.meta.listTableConstraints(schemaName, tableName)
                    .filter { ! it.constraintName.contains("_not_null") }
                val foreignKeys = db.meta.listForeignKeys(schemaName, tableName)
                val foreignKeyRefs = db.meta.listForeignKeyRefs(schemaName, tableName)
                val indexes = db.meta.listIndexes(schemaName, tableName)
                respond(TableDetail(table, columns, tableConstraints, foreignKeys, foreignKeyRefs, indexes))
            }
        }
    }
}
