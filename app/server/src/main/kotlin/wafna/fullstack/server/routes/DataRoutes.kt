package wafna.fullstack.server.routes

import com.opencsv.CSVReader
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import java.io.StringReader
import wafna.fullstack.api.API
import wafna.fullstack.api.domain.DataBlockImport
import wafna.fullstack.api.domain.DataRecordImport
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
    fpost("/data-blocks/{name}") {
        withSession { actor ->
            val name = requireParameter("name")
            val data = receive<ByteArray>().let { bytes ->
                StringReader(String(bytes, Charsets.UTF_8)).use { reader ->
                    CSVReader(reader).use { csvReader ->
                        csvReader.readAll()
                    }
                }
            }
            val records = data.mapNotNull { array ->
                if (array.isEmpty()) return@mapNotNull null
                DataRecordImport(array.first(), array.drop(1))
            }
            val dataBlockImport = DataBlockImport(actor, name, records)
            val dataBlock = api.dataBlocks.import(actor, dataBlockImport).getOrThrow()
            respondJson(dataBlock.toJson())
        }
    }
}