package itmo.edugoolda.api.user.storage

import itmo.edugoolda.api.user.domain.UserInfo
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.domain.UserRole.Companion.toDTO
import itmo.edugoolda.utils.EntityId
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class DatabaseUserStorage : UserStorage {
    override suspend fun createUser(
        email: String,
        name: String,
        role: UserRole
    ): EntityId = transaction {
        val id = UserTable.insert {
            it[UserTable.name] = name
            it[UserTable.email] = email
            it[UserTable.role] = role.toDTO()
        }[UserTable.id].value

        EntityId.parse(id)
    }

    override suspend fun getUserData(userId: EntityId): UserInfo? = transaction {
        UserTable.selectAll()
            .where { UserTable.id eq userId.value }
            .singleOrNull()
            .toUserInfo()
    }

    override suspend fun getUserByEmail(email: String): UserInfo? = transaction {
        UserTable.selectAll()
            .where { UserTable.email eq email }
            .singleOrNull()
            .toUserInfo()
    }

    override suspend fun getUserById(id: EntityId): UserInfo? = transaction {
        UserTable.selectAll()
            .where { UserTable.id eq id.value }
            .singleOrNull()
            .toUserInfo()
    }

    override suspend fun markDeleted(id: EntityId) {
        UserTable.update(
            where = { UserTable.id eq id.value },
            body = {
                it[isDeleted] = true
            }
        )
    }
}

private fun ResultRow?.toUserInfo(): UserInfo? {
    this ?: return null

    val role = get(UserTable.role).let(UserRole::fromString)
        ?: return null

    return UserInfo(
        email = get(UserTable.email),
        name = get(UserTable.name),
        role = role,
        id = get(UserTable.id).value.let(EntityId::parse),
        isDeleted = get(UserTable.isDeleted)
    )
}
