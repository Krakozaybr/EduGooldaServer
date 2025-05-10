package itmo.edugoolda.api.group.storage.entities

import itmo.edugoolda.api.group.domain.model.GroupEntityDomain
import itmo.edugoolda.api.group.domain.model.GroupInfoDomain
import itmo.edugoolda.api.group.storage.tables.BannedUsersTable
import itmo.edugoolda.api.group.storage.tables.GroupTable
import itmo.edugoolda.api.group.storage.tables.GroupToUserTable
import itmo.edugoolda.api.group.storage.tables.JoinRequestTable
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class GroupEntity(id: EntityID<UUID>) : BaseEntity(id, GroupTable) {
    companion object : BaseEntityClass<GroupEntity>(GroupTable)

    // Table fields
    var name by GroupTable.name
    var description by GroupTable.description
    var ownerId by GroupTable.ownerId
    var subjectId by GroupTable.subjectId
    var isActive by GroupTable.isActive

    // Linked fields
    var owner by UserEntity referencedOn GroupTable.ownerId
    var subject by SubjectEntity referencedOn GroupTable.subjectId

    val students by UserEntity via GroupToUserTable
    val requests by JoinRequestEntity referrersOn JoinRequestTable.groupId
    val banned by BannedEntity referrersOn BannedUsersTable.groupId
}

fun GroupEntity.toGroupEntityDomain() = GroupEntityDomain(
    id = EntityIdentifier.parse(id.value),
    name = name,
    description = description,
    ownerId = EntityIdentifier.parse(ownerId.value),
    subjectId = EntityIdentifier.parse(subjectId.value),
    isActive = isActive,
    createdAt = createdAt,
)

fun GroupEntity.toGroupInfoDomain(isFavourite: Boolean) = GroupInfoDomain(
    id = EntityIdentifier.parse(id.value),
    name = name,
    ownerName = owner.name,
    subjectName = subject.name,
    isFavourite = isFavourite
)
