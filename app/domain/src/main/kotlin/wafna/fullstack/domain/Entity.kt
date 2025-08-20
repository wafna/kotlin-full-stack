package wafna.fullstack.domain

import kotlin.time.Clock
import kotlin.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime

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
    @OptIn(ExperimentalTime::class)
    fun reify(createdAt: Instant = Clock.System.now()): E
}
