package client

import domain.DataBlockRecords
import domain.HomePageData

interface DataApi {
    suspend fun homePage(): Result<HomePageData>
    suspend fun dataBlock(dataBlockId: String): Result<DataBlockRecords>
}

fun dataApi(baseUrl: String, segmentPath: String): DataApi =
    object : DataApi, ApiSegment(baseUrl, segmentPath) {
        override suspend fun homePage(): Result<HomePageData> =
            get(segmentUrl("data-blocks")).json()

        override suspend fun dataBlock(dataBlockId: String): Result<DataBlockRecords> =
            get(segmentUrl("data-block-records/$dataBlockId")).json()
    }
