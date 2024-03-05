package wafna.fullstack.server

import arrow.core.left
import arrow.core.right
import com.google.gson.Gson
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import wafna.fullstack.domain.Schema
import wafna.fullstack.domain.Table
import wafna.fullstack.domain.errors.DomainError
import wafna.fullstack.domain.errors.DomainResult
import wafna.fullstack.server.controllers.APIController
import wafna.fullstack.server.routes.api
import wafna.fullstack.server.views.TableView

fun apiControllerStub(
    overview: DomainResult<List<Schema>>? = null,
    schema: DomainResult<List<Table>>? = null,
    table: DomainResult<TableView>? = null
): APIController {
    return object : APIController {
        override suspend fun overview(): DomainResult<List<Schema>> = overview!!
        override suspend fun schema(schemaName: String): DomainResult<List<Table>> = schema!!
        override suspend fun table(schemaName: String, tableName: String): DomainResult<TableView> = table!!
    }
}

private suspend fun runTestApplication(
    controller: APIController,
    test: suspend (HttpClient) -> Unit
) = with(controller) {
    testApplication {
        application {
            installContentNegotiation()
            routing {
                api()
            }
        }
        test(client)
    }
}

class ServerSpec {
    @Test
    fun overviewSuccess() = runBlocking {
        val apiController = apiControllerStub(
            listOf(
                Schema(
                    catalogName = "catalogName",
                    schemaName = "schemaName",
                    schemaOwner = "schemaOwner",
                    defaultCharacterSetCatalog = "defaultCharacterSetCatalog",
                    defaultCharacterSetSchema = "defaultCharacterSetSchema",
                    defaultCharacterSetName = "defaultCharacterSetName",
                    sqlPath = "sqlPath"
                )
            ).right()
        )
        runTestApplication(apiController) { client ->
            val response = client.get("/overview")
            response.status shouldBe HttpStatusCode.OK
            val payload = response.bodyAs<Array<Schema>>().toList()
            payload.size shouldBe 1
        }
    }
    @Test
    fun overviewInternalError() = runBlocking {
        val apiController = apiControllerStub(
            DomainError.InternalServerError("biffed").left()
        )
        runTestApplication(apiController) { client ->
            val response = client.get("/overview")
            response.status shouldBe HttpStatusCode.InternalServerError
        }
    }
}
