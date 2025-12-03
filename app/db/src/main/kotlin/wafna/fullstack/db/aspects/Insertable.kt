package wafna.fullstack.db.aspects

import kotlin.time.ExperimentalTime
import wafna.fullstack.domain.Wip
import wafna.fullstack.domain.WithEID
import wafna.fullstack.kdbc.Entity
import wafna.fullstack.kdbc.requireInserts
import java.sql.Connection

/** Insert records that have a **WIP** lifecycle. */
interface Insertable<W : Wip<E>, E : WithEID> {
    context(cx: Connection)
    suspend fun insert(head: W, vararg tail: W): List<E>

    context(cx: Connection)
    suspend fun insert(items: Collection<W>): List<E>

    // Convenience.
    context(cx: Connection)
    suspend fun insert1(item: W): E
}

private class InsertableImpl<W : Wip<E>, E : WithEID>(val entity: Entity<E>) : Insertable<W, E> {
    context(cx: Connection)
    override suspend fun insert(head: W, vararg tail: W): List<E> =
        insert(
            buildList {
                add(head)
                addAll(tail)
            },
        )

    @OptIn(ExperimentalTime::class)
    context(cx: Connection)
    override suspend fun insert(items: Collection<W>): List<E> =
        items
            .toList()
            .map { it.reify() }
            .also { all -> entity.insert(all).requireInserts(all.size) }

    context(cx: Connection)
    override suspend fun insert1(item: W): E =
        insert(listOf(item)).first()
}

/** Create an instance for composition. */
fun <W : Wip<E>, E : WithEID> insertable(entity: Entity<E>): Insertable<W, E> = InsertableImpl(entity)
