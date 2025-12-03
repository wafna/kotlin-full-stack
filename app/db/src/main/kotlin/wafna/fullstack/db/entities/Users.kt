package wafna.fullstack.db.entities

import kotlin.time.ExperimentalTime
import wafna.fullstack.domain.User
import wafna.fullstack.kdbc.Entity
import wafna.fullstack.kdbc.Param
import wafna.fullstack.kdbc.ResultSetFieldIterator
import wafna.fullstack.kdbc.field
import wafna.fullstack.kdbc.getString
import wafna.fullstack.kdbc.paramAny
import wafna.fullstack.kdbc.paramInstant
import wafna.fullstack.kdbc.paramString
import wafna.fullstack.kdbc.readRecord
import java.sql.Connection

object Users : Entity<User>(
    table = "fullstack.users",
    fields =
        listOf(
            "id".field,
            "username".field,
            "created_at".field,
            "deleted_at".field,
        ),
) {
    @OptIn(ExperimentalTime::class)
    override fun read(resultSet: ResultSetFieldIterator): User =
        resultSet.readRecord {
            User(
                id = getEID()!!,
                username = getString()!!,
                createdAt = getInstant()!!,
                deletedAt = getInstant(),
            )
        }

    @OptIn(ExperimentalTime::class)
    context(cx: Connection)
    override fun write(record: User): List<Param> =
        record.run {
            listOf(
                id.paramAny,
                username.paramString,
                createdAt.paramInstant,
                deletedAt.paramInstant,
            )
        }
}