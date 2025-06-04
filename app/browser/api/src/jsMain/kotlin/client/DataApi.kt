package client

import domain.DataBlock
import domain.DataBlockRecords
import domain.HomePageData
import web.file.File
import kotlinx.serialization.json.Json

interface DataApi {
    suspend fun homePage(): Result<HomePageData>
    suspend fun dataBlock(dataBlockId: String): Result<DataBlockRecords>
    suspend fun import(name: String, file: File): Result<DataBlock>
}

fun dataApi(baseUrl: String, segmentPath: String): DataApi =
    object : DataApi, ApiSegment(baseUrl, segmentPath) {
        override suspend fun homePage(): Result<HomePageData> =
            get(segmentUrl("data-blocks")).json()

        override suspend fun dataBlock(dataBlockId: String): Result<DataBlockRecords> =
            get(segmentUrl("data-block-records/$dataBlockId")).json()

        override suspend fun import(name: String, file: File): Result<DataBlock> =
            postFile(segmentUrl("data-blocks/$name"), file).map { response ->
                val body = response.text().cleanMangling()
                Json.decodeFromString(body)
            }
    }
