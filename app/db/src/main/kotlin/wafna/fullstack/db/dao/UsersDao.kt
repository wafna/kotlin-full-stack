package wafna.fullstack.db.dao

import wafna.fullstack.db.aspects.*
import wafna.fullstack.db.entities.Users
import wafna.fullstack.domain.User
import wafna.fullstack.domain.UserWip
import wafna.fullstack.kdbc.optional
import java.sql.Connection
import wafna.fullstack.kdbc.paramString

private typealias InsertableUser = Insertable<UserWip, User>

private typealias SelectableByEntityIdUser = SelectableByEntityId<User>

interface UsersDao : InsertableUser, SelectableByEntityIdUser, SoftDeletable {
    suspend fun byUsername(cx: Connection, username: String): User?
}

private class UsersDaoImpl(
    val insertable: InsertableUser,
    val selectableByEntityId: SelectableByEntityIdUser,
    val deletable: SoftDeletable
) : UsersDao,
    InsertableUser by insertable,
    SelectableByEntityIdUser by selectableByEntityId,
    SoftDeletable by deletable {
    override suspend fun byUsername(cx: Connection, username: String): User? =
        Users.select(cx, "u", "WHERE u.deleted_at IS NULL AND u.username = ?", username.paramString).optional
}

fun usersDao(): UsersDao = UsersDaoImpl(
    insertable(Users),
    selectableByEntityId(Users),
    softDeletable(Users.table)
)