package wafna.fullstack.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.*

// Entity id; the standard id type for entities in our domain.
typealias EID = UUID

/** Generate a new, unique entity id. */
fun entityId(): EID = UUID.randomUUID()

fun String.toEntityId(): EID = UUID.fromString(this)

interface WithEID {
    val id: EID
}

/** Standard for converting WIP entities to entities with ids. */
interface Wip<E> {
    fun reify(createdAt: Instant = Clock.System.now()): E
}
