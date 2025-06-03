package domain

import kotlinx.serialization.Serializable

@Serializable
data class DataBlockRecords(
    val dataBlock: DataBlock,
    val records: List<DataRecord>
)