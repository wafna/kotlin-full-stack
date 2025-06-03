package wafna.fullstack.domain

import kotlinx.datetime.Instant

data class DataRecordWip(
    val dataBlockId: EID,
    val key: String,
    val data: List<String>
) : Wip<DataRecord> {
    override fun reify(createdAt: Instant) =
        DataRecord(entityId(), dataBlockId, key, data)
}

data class DataRecord(
    override val id: EID,
    val dataBlockId: EID,
    val key: String,
    val data: List<String>
) : WithEID {
    init {
        require(key.isNotBlank()) { "Key cannot be blank." }
    }
}
