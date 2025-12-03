package wafna.fullstack.db.aspects

import kotlin.time.Clock
import java.sql.Connection
import kotlin.time.Instant
import wafna.fullstack.domain.EID
import wafna.fullstack.kdbc.paramAny
import wafna.fullstack.kdbc.paramInstant
import wafna.fullstack.kdbc.requireUpdates
import wafna.fullstack.kdbc.update
import kotlin.time.ExperimentalTime

/** Soft delete records that have a *deleted_at* field. */
interface SoftDeletable {
    @OptIn(ExperimentalTime::class)
    context(cx: Connection)
    suspend fun softDelete(
        id: EID,
        at: Instant = Clock.System.now(),
    )
}

private class SoftDeletableImpl(val tableName: String) : SoftDeletable {
    @OptIn(ExperimentalTime::class)
    context(cx: Connection)
    override suspend fun softDelete(
        id: EID,
        at: Instant,
    ) {
        update("UPDATE $tableName SET deleted_at = ? WHERE id = ?", at.paramInstant, id.paramAny)
            .requireUpdates(1)
    }
}

/** Create an instance for composition. */
fun softDeletable(tableName: String): SoftDeletable = SoftDeletableImpl(tableName)
