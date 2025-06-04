package wafna.fullstack.api.domain

import wafna.fullstack.domain.EID

data class DataBlockImport(
    val dataBlockId: EID,
    val name: String,
    val records: List<DataRecordImport>
)

data class DataRecordImport(
    val key: String,
    val values: List<String>
)