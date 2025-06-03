package wafna.fullstack.db.aspects

import wafna.fullstack.domain.Wip
import wafna.fullstack.domain.WithEID
import wafna.fullstack.kdbc.Entity
import wafna.fullstack.kdbc.requireInserts
import java.sql.Connection

/** Insert records that have a **WIP** lifecycle. */
interface Insertable<W : Wip<E>, E : WithEID> {
    suspend fun insert(
        cx: Connection, head: W, vararg tail: W
    ): List<E>

    suspend fun insert(
        cx: Connection, items: Collection<W>
    ): List<E>

    // Convenience.
    suspend fun insert1(
        cx: Connection, item: W
    ): E
}

private class InsertableImpl<W : Wip<E>, E : WithEID>(val entity: Entity<E>) : Insertable<W, E> {
    override suspend fun insert(
        cx: Connection, head: W, vararg tail: W
    ): List<E> =
        insert(
            cx,
            buildList {
                add(head)
                addAll(tail)
            },
        )

    override suspend fun insert(
        cx: Connection, items: Collection<W>
    ): List<E> =
        items
            .toList()
            .map { it.reify() }
            .also { all -> entity.insert(cx, all).requireInserts(all.size) }

    override suspend fun insert1(
        cx: Connection,
        item: W,
    ): E = insert(cx, listOf(item)).first()
}

/** Create an instance for composition. */
fun <W : Wip<E>, E : WithEID> insertable(entity: Entity<E>): Insertable<W, E> = InsertableImpl(entity)
