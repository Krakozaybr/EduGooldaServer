package itmo.edugoolda.api.group.storage.entities

import itmo.edugoolda.api.group.domain.model.JoinRequestDomain
import itmo.edugoolda.api.group.domain.model.JoinRequestInfoDomain
import itmo.edugoolda.api.group.storage.tables.JoinRequestTable
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.api.user.storage.entities.toDomain
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class JoinRequestEntity(id: EntityID<UUID>) : BaseEntity(id, JoinRequestTable) {
    companion object : BaseEntityClass<JoinRequestEntity>(JoinRequestTable)

    var userId by JoinRequestTable.userId
    var groupId by JoinRequestTable.groupId
    var status by JoinRequestTable.status

    var user by UserEntity referencedOn JoinRequestTable.userId
    var group by GroupEntity referencedOn JoinRequestTable.groupId
}

fun JoinRequestEntity.toDomain() = JoinRequestDomain(
    id = EntityIdentifier.parse(id.value),
    sender = user.toDomain(),
    groupId = EntityIdentifier.parse(groupId.value),
    createdAt = createdAt.toInstant(TimeZone.UTC)
)

fun JoinRequestEntity.toJoinRequestInfoDomain() = JoinRequestInfoDomain(
    id = EntityIdentifier.parse(id.value),
    senderId = EntityIdentifier.parse(userId.value),
    groupIdentifier = EntityIdentifier.parse(groupId.value),
    status = status,
)
