package itmo.edugoolda.api.group.storage.entities

import itmo.edugoolda.api.group.domain.model.SubjectDomain
import itmo.edugoolda.api.group.storage.tables.SubjectTable
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class SubjectEntity(id: EntityID<UUID>) : BaseEntity(id, SubjectTable) {
    companion object : BaseEntityClass<SubjectEntity>(SubjectTable)

    var name by SubjectTable.name
    var ownerId by SubjectTable.ownerId

    var owner by UserEntity referencedOn SubjectTable.ownerId
}

fun SubjectEntity.toDomain() = SubjectDomain(
    id = EntityIdentifier.parse(id.value),
    name = name,
    ownerId = EntityIdentifier.parse(ownerId.value)
)
