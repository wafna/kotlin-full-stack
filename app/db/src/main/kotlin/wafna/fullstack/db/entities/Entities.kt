package wafna.fullstack.db.entities

import kotlin.time.Instant
import wafna.fullstack.domain.EID
import wafna.fullstack.kdbc.ResultSetFieldIterator
import wafna.fullstack.kdbc.getObject
import wafna.fullstack.kdbc.getTimestamp
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinInstant

@OptIn(ExperimentalTime::class)
fun ResultSetFieldIterator.getInstant(): Instant? = getTimestamp()?.toInstant()?.toKotlinInstant()

fun ResultSetFieldIterator.getEID(): EID? = getObject() as? EID

