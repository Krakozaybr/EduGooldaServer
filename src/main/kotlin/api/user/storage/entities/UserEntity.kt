package itmo.edugoolda.api.user.storage.entities

import itmo.edugoolda.api.group.storage.entities.GroupEntity
import itmo.edugoolda.api.group.storage.tables.GroupToUserTable
import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserEntity(id: EntityID<UUID>) : BaseEntity(id, UserTable) {
    companion object : BaseEntityClass<UserEntity>(UserTable)

    var email by UserTable.email
    var name by UserTable.name
    var role by UserTable.role
    var isDeleted by UserTable.isDeleted
    var bio by UserTable.bio

    val groups by GroupEntity via GroupToUserTable
}

fun UserEntity.toDomain() = UserInfoDomain(
    id = EntityIdentifier.parse(id.value),
    email = email,
    name = name,
    role = role,
    isDeleted = isDeleted,
    bio = bio
)
