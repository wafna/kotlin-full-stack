package wafna.fullstack.db.entities

import wafna.fullstack.domain.User
import wafna.fullstack.kdbc.*
import java.sql.Connection
import java.sql.ResultSet
import kotlin.time.ExperimentalTime

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
    override fun read(resultSet: ResultSet): User =
        resultSet.readRecord {
            User(
                id = getEID()!!,
                username = getString()!!,
                createdAt = getInstant()!!,
                deletedAt = getInstant(),
            )
        }

    @OptIn(ExperimentalTime::class)
    override fun write(
        connection: Connection,
        record: User,
    ): List<Param> =
        record.run {
            listOf(
                id.paramAny,
                username.paramString,
                createdAt.paramInstant,
                deletedAt.paramInstant,
            )
        }
}