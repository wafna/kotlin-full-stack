package wafna.fullstack.server.routes

import io.ktor.server.routing.Route
import wafna.fullstack.api.API
import wafna.fullstack.domain.toEntityId
import wafna.fullstack.server.domain.HomePageData
import wafna.fullstack.server.withSession

internal fun Route.dataRoutes(api: API) {
    fget("/data-blocks") {
        withSession { actorId ->
            val dataBlocks = api.dataBlocks.byOwner(actorId).internalServerError()
            respondJson(HomePageData(dataBlocks).toJson())
        }
    }
    fget("/data-block-records/{data-block-id}") {
        val dataBlockId = requireParameter("data-block-id").toEntityId()
        withSession { actorId ->
            val block = api.dataBlocks.records(dataBlockId).internalServerError()
            respondJson(block.toJson())
        }
    }
}