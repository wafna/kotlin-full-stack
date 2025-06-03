package wafna.fullstack.db.entities

import java.sql.Connection
import java.sql.ResultSet
import wafna.fullstack.domain.DataRecord
import wafna.fullstack.kdbc.Entity
import wafna.fullstack.kdbc.Param
import wafna.fullstack.kdbc.field
import wafna.fullstack.kdbc.getString
import wafna.fullstack.kdbc.getStringList
import wafna.fullstack.kdbc.paramAny
import wafna.fullstack.kdbc.paramString
import wafna.fullstack.kdbc.paramStrings
import wafna.fullstack.kdbc.readRecord

object DataRecords : Entity<DataRecord>(
    table = "fullstack.data_records",
    fields = listOf("id".field, "data_block_id".field, "key".field, "data".field)
) {
    override fun read(resultSet: ResultSet): DataRecord =
        resultSet.readRecord {
            DataRecord(
                id = getEID()!!,
                dataBlockId = getEID()!!,
                key = getString()!!,
                data = getStringList()!!
            )
        }

    override fun write(
        connection: Connection,
        record: DataRecord
    ): List<Param> = record.run {
        listOf(
            id.paramAny,
            dataBlockId.paramAny,
            key.paramString,
            data.paramStrings(connection)
        )
    }
}