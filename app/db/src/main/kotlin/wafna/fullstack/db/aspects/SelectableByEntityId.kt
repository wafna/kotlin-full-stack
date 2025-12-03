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
    context(cx: Connection)
    suspend fun listAll(): List<E>

    context(cx: Connection)
    suspend fun forId(id: EID): E?

    context(cx: Connection)
    suspend fun forIds(ids: Collection<EID>): List<E>

    context(cx: Connection)
    suspend fun listAllActive(): List<E>

    context(cx: Connection)
    suspend fun forIdActive(id: EID): E?

    context(cx: Connection)
    suspend fun forIdsActive(ids: Collection<EID>): List<E>
}

private class SelectableByEntityIdImpl<E : WithEID>(val entity: Entity<E>) :
    SelectableByEntityId<E> {
    context(cx: Connection)
    override suspend fun listAll(): List<E> =
        entity.select("", "")

    context(cx: Connection)
    override suspend fun forId(id: EID): E? =
        entity.select("", "WHERE id = ?", id.paramAny).optional

    context(cx: Connection)
    override suspend fun forIds(ids: Collection<EID>): List<E> =
        if (ids.isEmpty()) {
            emptyList()
        } else {
            entity.select("", "WHERE id IN ${inList(ids.size)}", ids.map { it.paramAny })
        }

    context(cx: Connection)
    override suspend fun listAllActive(): List<E> =
        entity.select("", "WHERE deleted_at IS NULL")

    context(cx: Connection)
    override suspend fun forIdActive(id: EID): E? =
        entity.select("", "WHERE deleted_at IS NULL AND id = ?", id.paramAny).optional

    context(cx: Connection)
    override suspend fun forIdsActive(ids: Collection<EID>): List<E> =
        if (ids.isEmpty()) {
            emptyList()
        } else {
            entity.select(
                "",
                " WHERE deleted_at IS NULL AND id IN ${inList(ids.size)}",
                ids.map { it.paramAny },
            )
        }
}

/** Create an instance for composition. */
fun <E : WithEID> selectableByEntityId(entity: Entity<E>): SelectableByEntityId<E> = SelectableByEntityIdImpl(entity)
