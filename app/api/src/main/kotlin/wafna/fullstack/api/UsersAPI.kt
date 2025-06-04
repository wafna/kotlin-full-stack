package wafna.fullstack.api

import wafna.fullstack.domain.EID
import wafna.fullstack.domain.User
import wafna.fullstack.domain.UserWip
import wafna.fullstack.kdbc.AppDb

interface UsersAPI {
    suspend fun create(userWip: UserWip): Result<User>
    suspend fun delete(user: User): Result<Unit>
    suspend fun byId(id: EID): Result<User?>
    suspend fun byUsername(username: String): Result<User?>
}

internal fun usersAPI(db: AppDb): UsersAPI = object : UsersAPI {
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
