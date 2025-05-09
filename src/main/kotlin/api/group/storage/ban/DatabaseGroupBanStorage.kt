package itmo.edugoolda.api.group.storage.ban

import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.storage.entities.BannedEntity
import itmo.edugoolda.api.group.storage.entities.GroupEntity
import itmo.edugoolda.api.group.storage.tables.BannedUsersTable
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.utils.EntityIdentifier
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseGroupBanStorage : GroupBanStorage {
    override suspend fun banUser(
        groupId: EntityIdentifier,
        userId: EntityIdentifier
    ): Unit = transaction {

        val group = GroupEntity.findById(groupId.value)
            ?: throw GroupNotFoundException(groupId)

        val user = UserEntity.findById(userId.value)
            ?: throw UserNotFoundException(userId)

        BannedEntity.new {
            this.user = user
            this.group = group
        }
    }

    override suspend fun unbanUser(
        groupId: EntityIdentifier,
        userId: EntityIdentifier
    ) = transaction {
        val entity = BannedEntity.find {
            (BannedUsersTable.groupId eq groupId.value) and (BannedUsersTable.userId eq userId.value)
        }.singleOrNull()

        entity?.delete() != null
    }
}