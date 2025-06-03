package wafna.fullstack.db.dao

import wafna.fullstack.db.aspects.Insertable
import wafna.fullstack.db.aspects.insertable
import wafna.fullstack.db.entities.DataRecords
import wafna.fullstack.domain.DataRecord
import wafna.fullstack.domain.DataRecordWip
import wafna.fullstack.domain.EID
import wafna.fullstack.kdbc.paramAny
import java.sql.Connection

private typealias InsertableDataRecord = Insertable<DataRecordWip, DataRecord>

interface DataRecordsDao : InsertableDataRecord {
    suspend fun byDataBlockId(cx: Connection, dataBlockId: EID): List<DataRecord>
}

private class DataRecordsDaoImpl(
    val insertable: InsertableDataRecord,
) : DataRecordsDao, InsertableDataRecord by insertable {
    override suspend fun byDataBlockId(
        cx: Connection,
        dataBlockId: EID
    ): List<DataRecord> = DataRecords.select(cx, "r", "WHERE r.data_block_id = ?", dataBlockId.paramAny)
}

fun dataRecordsDao(): DataRecordsDao = DataRecordsDaoImpl(insertable(DataRecords))
