package wafna.fullstack.api

import wafna.fullstack.api.domain.DataBlockImport
import wafna.fullstack.api.domain.DataBlockRecords
import wafna.fullstack.db.entities.DataBlocks
import wafna.fullstack.domain.DataBlock
import wafna.fullstack.domain.DataBlockWip
import wafna.fullstack.domain.DataRecordWip
import wafna.fullstack.domain.EID
import wafna.fullstack.kdbc.AppDb
import wafna.fullstack.kdbc.paramAny

interface DataBlocksAPI {
    suspend fun create(
        ownerId: EID,
        name: String,
        records: Iterable<Pair<String, List<String>>>
    ): Result<DataBlockRecords>

    suspend fun delete(id: EID): Result<Unit>
    suspend fun byId(id: EID): Result<DataBlock?>
    suspend fun byOwner(ownerId: EID): Result<List<DataBlock>>
    suspend fun records(dataBlockId: EID): Result<DataBlockRecords>
    suspend fun import(ownerId: EID, dataBlockImport: DataBlockImport): Result<DataBlock>

}

internal fun dataBlocksAPI(db: AppDb): DataBlocksAPI =
    object : DataBlocksAPI {
        override suspend fun create(
            ownerId: EID,
            name: String,
            records: Iterable<Pair<String, List<String>>>
        ): Result<DataBlockRecords> = db.transact { cx ->
            val dataBlock = db.dataBlocks.insert1(cx, DataBlockWip(ownerId, name))
            val actual = records.map { DataRecordWip(dataBlock.id, it.first, it.second) }
            val records = db.dataRecords.insert(cx, actual)
            DataBlockRecords(dataBlock, records)
        }

        override suspend fun delete(id: EID): Result<Unit> = db.transact { cx ->
            db.dataBlocks.softDelete(cx, id)
        }

        override suspend fun byId(id: EID): Result<DataBlock?> = db.transact { cx ->
            db.dataBlocks.forId(cx, id)
        }

        override suspend fun byOwner(ownerId: EID): Result<List<DataBlock>> = db.transact { cx ->
            DataBlocks.select(cx, "db", "WHERE owner = ?", ownerId.paramAny)
        }

        override suspend fun records(dataBlockId: EID): Result<DataBlockRecords> = db.transact { cx ->
            val dataBlock =
                db.dataBlocks.forId(cx, dataBlockId) ?: error("Data block with id $dataBlockId not found")
            val records = db.dataRecords.byDataBlockId(cx, dataBlockId)
            DataBlockRecords(dataBlock, records)
        }

        override suspend fun import(ownerId: EID, dataBlockImport: DataBlockImport): Result<DataBlock> =
            db.transact { cx ->
                val dataBlock = db.dataBlocks.insert1(cx, DataBlockWip(ownerId, dataBlockImport.name))
                dataBlockImport.records.map { DataRecordWip(dataBlock.id, it.key, it.values) }
                    .let { records -> db.dataRecords.insert(cx, records) }
                dataBlock
            }
    }