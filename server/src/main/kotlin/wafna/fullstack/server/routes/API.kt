package wafna.fullstack.server.routes

import arrow.core.raise.either
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import wafna.fullstack.server.ServerContext
import wafna.fullstack.server.controllers.APIController
import wafna.fullstack.server.views.TableView

context(APIController)
internal fun Route.api() {
    get("/overview") {
        call.bracket {
            either {
                val schemas = overview().bind()
                respond(schemas)
            }
        }
    }
    get("/schema/{schema}") {
        val schemaName = call.parameters["schema"]!!
        call.bracket {
            either {
                val tables = schema(schemaName).bind()
                respond(tables)
            }
        }
    }
    get("/table/{schema}/{table}") {
        val schemaName = call.parameters["schema"]!!
        val tableName = call.parameters["table"]!!
        call.bracket {
            either {
                val table = table(schemaName, tableName).bind()
                respond(table)
            }
        }
    }
}
