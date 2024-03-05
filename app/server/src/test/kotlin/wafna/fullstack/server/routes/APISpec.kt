package wafna.fullstack.server.routes

import arrow.core.left
import arrow.core.right
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import wafna.fullstack.domain.Schema
import wafna.fullstack.domain.Table
import wafna.fullstack.domain.errors.DomainError
import wafna.fullstack.domain.errors.DomainResult
import wafna.fullstack.server.bodyAs
import wafna.fullstack.server.controllers.APIController
import wafna.fullstack.server.installContentNegotiation
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
) = testApplication {
    application {
        installContentNegotiation()
        routing {
            with(controller) { api() }
        }
    }
    test(client)
}

class APISpec {
    @Test
    fun `overview (success)`() = runBlocking {
        val schema = Schema(
            catalogName = "catalogName",
            schemaName = "schemaName",
            schemaOwner = "schemaOwner",
            defaultCharacterSetCatalog = "defaultCharacterSetCatalog",
            defaultCharacterSetSchema = "defaultCharacterSetSchema",
            defaultCharacterSetName = "defaultCharacterSetName",
            sqlPath = "sqlPath"
        )
        val apiController = apiControllerStub(
            overview = listOf(schema).right()
        )
        runTestApplication(apiController) { client ->
            val response = client.get("/overview")
            response.status shouldBe HttpStatusCode.OK
            val payload = response.bodyAs<Array<Schema>>().toList()
            payload.size shouldBe 1
            payload.first().apply {
                catalogName shouldBe schema.catalogName
                schemaName shouldBe schema.schemaName
                schemaOwner shouldBe schema.schemaOwner
                defaultCharacterSetCatalog shouldBe schema.defaultCharacterSetCatalog
                defaultCharacterSetSchema shouldBe schema.defaultCharacterSetSchema
                defaultCharacterSetName shouldBe schema.defaultCharacterSetName
                sqlPath shouldBe schema.sqlPath
            }
        }
    }

    @Test
    fun `overview (internal server error)`() = runBlocking {
        val apiController = apiControllerStub(
            overview = DomainError.InternalServerError("Fall down, go boom.").left()
        )
        runTestApplication(apiController) { client ->
            val response = client.get("/overview")
            response.status shouldBe HttpStatusCode.InternalServerError
        }
    }

    @Test
    fun `schema (not found)`() = runBlocking {
        val apiController = apiControllerStub(
            schema = DomainError.NotFound("nonesuch").left()
        )
        runTestApplication(apiController) { client ->
            val response = client.get("/schema/nonesuch")
            response.status shouldBe HttpStatusCode.NotFound
        }
    }

    @Test
    fun `table (bad request)`() = runBlocking {
        val apiController = apiControllerStub(
            table = DomainError.BadRequest("nonesuch").left()
        )
        runTestApplication(apiController) { client ->
            val response = client.get("/table/nonesuch/-")
            response.status shouldBe HttpStatusCode.BadRequest
        }
    }
}
