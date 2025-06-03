package wafna.fullstack.db.aspects

import kotlinx.datetime.Clock
import java.sql.Connection
import kotlinx.datetime.Instant
import wafna.fullstack.domain.EID
import wafna.fullstack.kdbc.paramAny
import wafna.fullstack.kdbc.paramInstant
import wafna.fullstack.kdbc.requireUpdates
import wafna.fullstack.kdbc.update

/** Soft delete records that have a *deleted_at* field. */
interface SoftDeletable {
    suspend fun softDelete(
        cx: Connection,
        id: EID,
        at: Instant = Clock.System.now(),
    )
}

private class SoftDeletableImpl(val tableName: String) : SoftDeletable {
    override suspend fun softDelete(
        cx: Connection,
        id: EID,
        at: Instant,
    ) {
        cx.update("UPDATE $tableName SET deleted_at = ? WHERE id = ?", at.paramInstant, id.paramAny)
            .requireUpdates(1)
    }
}

/** Create an instance for composition. */
fun softDeletable(tableName: String): SoftDeletable = SoftDeletableImpl(tableName)
