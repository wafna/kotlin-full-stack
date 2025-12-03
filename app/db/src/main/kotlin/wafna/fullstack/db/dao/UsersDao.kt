package wafna.fullstack.db.dao

import wafna.fullstack.db.aspects.Insertable
import wafna.fullstack.db.aspects.SelectableByEntityId
import wafna.fullstack.db.aspects.SoftDeletable
import wafna.fullstack.db.aspects.insertable
import wafna.fullstack.db.aspects.selectableByEntityId
import wafna.fullstack.db.aspects.softDeletable
import wafna.fullstack.db.entities.Users
import wafna.fullstack.domain.User
import wafna.fullstack.domain.UserWip
import wafna.fullstack.kdbc.optional
import wafna.fullstack.kdbc.paramString
import java.sql.Connection

private typealias InsertableUser = Insertable<UserWip, User>

private typealias SelectableByEntityIdUser = SelectableByEntityId<User>

interface UsersDao : InsertableUser, SelectableByEntityIdUser, SoftDeletable {
    context(cx: Connection)
    suspend fun byUsername(username: String): User?
}

private class UsersDaoImpl(
    val insertable: InsertableUser,
    val selectableByEntityId: SelectableByEntityIdUser,
    val deletable: SoftDeletable
) : UsersDao,
    InsertableUser by insertable,
    SelectableByEntityIdUser by selectableByEntityId,
    SoftDeletable by deletable {
    context(cx: Connection)
    override suspend fun byUsername(username: String): User? =
        Users.select("u", "WHERE u.deleted_at IS NULL AND u.username = ?", username.paramString).optional
}

fun usersDao(): UsersDao = UsersDaoImpl(
    insertable(Users),
    selectableByEntityId(Users),
    softDeletable(Users.table)
)