package wafna.fullstack.domain

import kotlinx.datetime.Instant

data class DataBlockWip(val owner: EID, val name: String) : Wip<DataBlock> {
    override fun reify(createdAt: Instant) = DataBlock(
        id = entityId(),
        owner = owner,
        name = name,
        createdAt = createdAt,
        deletedAt = null
    )
}

data class DataBlock(
    override val id: EID,
    val owner: EID,
    val name: String,
    val createdAt: Instant,
    val deletedAt: Instant?,
) : WithEID
