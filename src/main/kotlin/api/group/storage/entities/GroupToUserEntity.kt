package itmo.edugoolda.api.group.storage.entities

import itmo.edugoolda.api.group.storage.tables.GroupToUserTable
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class GroupToUserEntity(id: EntityID<UUID>) : BaseEntity(id, GroupToUserTable) {
    companion object : BaseEntityClass<GroupToUserEntity>(GroupToUserTable)

    var userId by GroupToUserTable.userId
    var groupId by GroupToUserTable.groupId

    var user by UserEntity referencedOn GroupToUserTable.userId
    var group by GroupEntity referencedOn GroupToUserTable.groupId
}
