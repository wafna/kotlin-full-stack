package wafna.fullstack.api.domain

import wafna.fullstack.domain.DataBlock
import wafna.fullstack.domain.DataRecord

data class DataBlockRecords(
    val dataBlock: DataBlock,
    val records: List<DataRecord>
)