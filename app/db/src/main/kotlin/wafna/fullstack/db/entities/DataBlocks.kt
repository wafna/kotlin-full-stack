package wafna.fullstack.db.entities

import kotlin.time.ExperimentalTime
import wafna.fullstack.domain.DataBlock
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
import java.sql.ResultSet

object DataBlocks : Entity<DataBlock>(
    table = "fullstack.data_blocks",
    fields = listOf(
        "id".field,
        "owner".field,
        "name".field,
        "created_at".field,
        "deleted_at".field,
    )
) {
    @OptIn(ExperimentalTime::class)
    override fun read(resultSet: ResultSetFieldIterator): DataBlock =
        resultSet.readRecord {
            DataBlock(
                id = getEID()!!,
                owner = getEID()!!,
                name = getString()!!,
                createdAt = getInstant()!!,
                deletedAt = getInstant(),
            )
        }

    @OptIn(ExperimentalTime::class)
    context(cx: Connection)
    override fun write(record: DataBlock): List<Param> = record.run {
        listOf(
            id.paramAny,
            owner.paramAny,
            name.paramString,
            createdAt.paramInstant,
            deletedAt.paramInstant,
        )
    }
}