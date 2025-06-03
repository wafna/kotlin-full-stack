package wafna.fullstack.api

import wafna.fullstack.api.domain.DataBlockRecords
import wafna.fullstack.db.entities.DataBlocks
import wafna.fullstack.domain.DataBlock
import wafna.fullstack.domain.DataBlockWip
import wafna.fullstack.domain.DataRecordWip
import wafna.fullstack.domain.EID
import wafna.fullstack.domain.User
import wafna.fullstack.domain.UserWip
import wafna.fullstack.kdbc.AppDb
import wafna.fullstack.kdbc.paramAny

interface UsersAPI {
    suspend fun create(userWip: UserWip): Result<User>
    suspend fun delete(user: User): Result<Unit>
    suspend fun byId(id: EID): Result<User?>
    suspend fun byUsername(username: String): Result<User?>
}

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
}

interface API {
    val users: UsersAPI
    val dataBlocks: DataBlocksAPI
}

fun api(db: AppDb): API {
    return object : API {
        override val users = object : UsersAPI {
            override suspend fun create(userWip: UserWip): Result<User> =
                db.transact { cx ->
                    db.users.insert(cx, userWip)
                }.map { it.firstOrNull() ?: return Result.failure(Exception("User not created.")) }

            override suspend fun delete(user: User): Result<Unit> = db.transact { cx ->
                db.users.softDelete(cx, user.id)
            }

            override suspend fun byId(id: EID): Result<User?> = db.transact { cx ->
                db.users.forId(cx, id)
            }

            override suspend fun byUsername(username: String): Result<User?> = db.transact { cx ->
                db.users.byUsername(cx, username)
            }
        }
        override val dataBlocks = object : DataBlocksAPI {
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
        }
    }
}