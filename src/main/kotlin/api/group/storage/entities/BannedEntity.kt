package itmo.edugoolda.api.group.storage.entities

import itmo.edugoolda.api.group.storage.tables.BannedUsersTable
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class BannedEntity(id: EntityID<UUID>) : BaseEntity(id, BannedUsersTable) {
    companion object : BaseEntityClass<BannedEntity>(BannedUsersTable)

    var userId by BannedUsersTable.userId
    var groupId by BannedUsersTable.groupId

    var user by UserEntity referencedOn BannedUsersTable.userId
    var group by GroupEntity referencedOn BannedUsersTable.groupId
}