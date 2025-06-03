package wafna.fullstack.server.routes

import io.ktor.client.HttpClient
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import wafna.fullstack.api.API

private suspend fun runTestApplication(
    api: API,
    test: suspend (HttpClient) -> Unit
) = testApplication {
    application {
        routing {
//            with(controller) { api() }
        }
    }
    test(client)
}

/**
 * Tests that objects are serialized and that the domain errors are correctly translated to HTTP status codes.
 */
class APISpec {
    @Test
    fun `overview (success)`() = runBlocking {
//        runTestApplication(apiController) { client ->
//            val response = client.get("/overview")
//            response.status shouldBe HttpStatusCode.OK
//            val payload = response.bodyAs<Array<Schema>>().toList()
//            payload.size shouldBe 1
//            payload.first().apply {
//                catalogName shouldBe schema.catalogName
//                schemaName shouldBe schema.schemaName
//                schemaOwner shouldBe schema.schemaOwner
//                defaultCharacterSetCatalog shouldBe schema.defaultCharacterSetCatalog
//                defaultCharacterSetSchema shouldBe schema.defaultCharacterSetSchema
//                defaultCharacterSetName shouldBe schema.defaultCharacterSetName
//                sqlPath shouldBe schema.sqlPath
//            }
//        }
    }

}
