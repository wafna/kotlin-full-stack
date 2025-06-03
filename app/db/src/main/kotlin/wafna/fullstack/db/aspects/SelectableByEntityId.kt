package wafna.fullstack.db.aspects

import wafna.fullstack.domain.EID
import wafna.fullstack.domain.WithEID
import wafna.fullstack.kdbc.Entity
import wafna.fullstack.kdbc.inList
import wafna.fullstack.kdbc.optional
import wafna.fullstack.kdbc.paramAny
import java.sql.Connection

/** Select records that have an *id* field. */
interface SelectableByEntityId<E : WithEID> {
    // Primarily for testing
    suspend fun listAll(cx: Connection): List<E>

    suspend fun forId(
        cx: Connection,
        id: EID,
    ): E?

    suspend fun forIds(
        cx: Connection,
        ids: Collection<EID>,
    ): List<E>

    suspend fun listAllActive(cx: Connection): List<E>

    suspend fun forIdActive(
        cx: Connection,
        id: EID,
    ): E?

    suspend fun forIdsActive(
        cx: Connection,
        ids: Collection<EID>,
    ): List<E>
}

private class SelectableByEntityIdImpl<E : WithEID>(val entity: Entity<E>) :
    SelectableByEntityId<E> {
    override suspend fun listAll(cx: Connection): List<E> = entity.select(cx, "", "")

    override suspend fun forId(
        cx: Connection,
        id: EID,
    ): E? = entity.select(cx, "", "WHERE id = ?", id.paramAny).optional

    override suspend fun forIds(
        cx: Connection,
        ids: Collection<EID>,
    ): List<E> =
        if (ids.isEmpty()) {
            emptyList()
        } else {
            entity.select(cx, "", "WHERE id IN ${inList(ids.size)}", ids.map { it.paramAny })
        }

    override suspend fun listAllActive(cx: Connection): List<E> = entity.select(cx, "", "WHERE deleted_at IS NULL")

    override suspend fun forIdActive(
        cx: Connection,
        id: EID,
    ): E? = entity.select(cx, "", "WHERE deleted_at IS NULL AND id = ?", id.paramAny).optional

    override suspend fun forIdsActive(
        cx: Connection,
        ids: Collection<EID>,
    ): List<E> =
        if (ids.isEmpty()) {
            emptyList()
        } else {
            entity.select(
                cx,
                "",
                " WHERE deleted_at IS NULL AND id IN ${inList(ids.size)}",
                ids.map { it.paramAny },
            )
        }
}

/** Create an instance for composition. */
fun <E : WithEID> selectableByEntityId(entity: Entity<E>): SelectableByEntityId<E> = SelectableByEntityIdImpl(entity)
