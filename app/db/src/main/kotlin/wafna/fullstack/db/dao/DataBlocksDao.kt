package wafna.fullstack.db.dao

import wafna.fullstack.db.aspects.*
import wafna.fullstack.db.entities.DataBlocks
import wafna.fullstack.domain.DataBlock
import wafna.fullstack.domain.DataBlockWip
import wafna.fullstack.domain.EID
import wafna.fullstack.kdbc.paramAny
import java.sql.Connection

private typealias InsertableDataBlock = Insertable<DataBlockWip, DataBlock>

private typealias SelectableByEntityIdDataBlock = SelectableByEntityId<DataBlock>

interface DataBlocksDao : InsertableDataBlock, SelectableByEntityIdDataBlock, SoftDeletable {
    suspend fun byOwnerId(cx: Connection, ownerId: EID): List<DataBlock>
}

private class DataBlocksDaoImpl(
    val insertable: InsertableDataBlock,
    val selectableByEntityId: SelectableByEntityIdDataBlock,
    val deletable: SoftDeletable
) : DataBlocksDao,
    InsertableDataBlock by insertable,
    SelectableByEntityIdDataBlock by selectableByEntityId,
    SoftDeletable by deletable {
    override suspend fun byOwnerId(
        cx: Connection,
        ownerId: EID
    ): List<DataBlock> =
        DataBlocks.select(cx, "b", "WHERE b.owner = ?", ownerId.paramAny)
}

fun dataBlocksDao(): DataBlocksDao = DataBlocksDaoImpl(
    insertable = insertable(DataBlocks),
    selectableByEntityId = selectableByEntityId(DataBlocks),
    deletable = softDeletable(DataBlocks.table)
)