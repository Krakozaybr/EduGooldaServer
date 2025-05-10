package itmo.edugoolda.api.group.storage.entities

import itmo.edugoolda.api.group.storage.tables.UserFavouriteGroupTable
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserFavouriteGroupEntity(id: EntityID<UUID>) : BaseEntity(id, UserFavouriteGroupTable) {
    companion object : BaseEntityClass<UserFavouriteGroupEntity>(UserFavouriteGroupTable)

    var userId by UserFavouriteGroupTable.userId
    var groupId by UserFavouriteGroupTable.groupId
    var isFavourite by UserFavouriteGroupTable.isFavourite

    var user by UserEntity referencedOn UserFavouriteGroupTable.userId
    var group by GroupEntity referencedOn UserFavouriteGroupTable.groupId
}