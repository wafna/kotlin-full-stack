package wafna.fullstack.db.entities

import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import wafna.fullstack.domain.EID
import wafna.fullstack.kdbc.ResultSetFieldIterator
import wafna.fullstack.kdbc.getObject
import wafna.fullstack.kdbc.getTimestamp

fun ResultSetFieldIterator.getInstant(): Instant? = getTimestamp()?.toInstant()?.toKotlinInstant()

fun ResultSetFieldIterator.getEID(): EID? = getObject() as? EID

