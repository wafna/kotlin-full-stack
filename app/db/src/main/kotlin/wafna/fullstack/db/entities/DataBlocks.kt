package wafna.fullstack.db.entities

import wafna.fullstack.domain.DataBlock
import wafna.fullstack.kdbc.*
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
    override fun read(resultSet: ResultSet): DataBlock =
        resultSet.readRecord {
            DataBlock(
                id = getEID()!!,
                owner = getEID()!!,
                name = getString()!!,
                createdAt = getInstant()!!,
                deletedAt = getInstant(),
            )
        }

    override fun write(
        connection: Connection,
        record: DataBlock,
    ): List<Param> = record.run {
        listOf(
            id.paramAny,
            owner.paramAny,
            name.paramString,
            createdAt.paramInstant,
            deletedAt.paramInstant,
        )
    }
}