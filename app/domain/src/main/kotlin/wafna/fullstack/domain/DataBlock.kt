package wafna.fullstack.domain

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class DataBlockWip(val owner: EID, val name: String) : Wip<DataBlock> {
    @OptIn(ExperimentalTime::class)
    override fun reify(createdAt: Instant) = DataBlock(
        id = entityId(),
        owner = owner,
        name = name,
        createdAt = createdAt,
        deletedAt = null
    )
}

data class DataBlock @OptIn(ExperimentalTime::class) constructor(
    override val id: EID,
    val owner: EID,
    val name: String,
    val createdAt: Instant,
    val deletedAt: Instant?,
) : WithEID
