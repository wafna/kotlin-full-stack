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
import java.util.UUID
import wafna.dbexplorer.domain.Mangled
import wafna.dbexplorer.domain.Schema
import wafna.dbexplorer.domain.TableDetail
import wafna.dbexplorer.util.LazyLogger

fun ApplicationCall.ok() = response.status(HttpStatusCode.OK)
fun ApplicationCall.internalServerError() = response.status(HttpStatusCode.InternalServerError)
fun ApplicationCall.badRequest() = response.status(HttpStatusCode.BadRequest)
fun ApplicationCall.notFound() = response.status(HttpStatusCode.NotFound)

private object API

private val log = LazyLogger(API::class)

/**
 * Sets the given status code into the response if the block returns normally.
 */
suspend fun ApplicationCall.bracket(
    status: HttpStatusCode = HttpStatusCode.OK,
    block: suspend ApplicationCall.() -> Unit,
) =
    either {
        Either.catch { block() }.bind()
        response.status(status)
    }.mapLeft { e ->
        log.error(e) { "HTTP Error: ${request.httpMethod.value} ${request.uri}" }
        internalServerError()
    }

// Hack to (temporarily) work around the mangling Kotlin does to the names of the fields in the browser.
data class Schema_1(val id_1: UUID, val data_1: String) : Mangled {
    fun domain(): Schema = TODO() // Schema(id_1, data_1)
}

/**
 * The browser API.
 */
context(ServerContext)
internal fun Route.api() {
    get("/schemas") {
        call.bracket {
            val schemas = db.metaDAO.listSchemas()
            respond(schemas)
        }
    }
    get("/tables/{schema}") {
        val schemaName = call.parameters["schema"]!!
        call.bracket {
            val tables = db.metaDAO.listTables(schemaName)
            respond(tables)
        }
    }
    get("/tables/{schema}/{table}") {
        val schemaName = call.parameters["schema"]!!
        val tableName = call.parameters["table"]!!
        call.bracket {
            val table = db.metaDAO.getTable(schemaName, tableName)
            if (null == table) {
                notFound()
                return@bracket
            }
            val columns = db.metaDAO.listColumns(schemaName, tableName)
            val constraints = db.metaDAO.listConstraints(schemaName, tableName)
            respond(TableDetail(table, columns, constraints))
        }
    }
}
