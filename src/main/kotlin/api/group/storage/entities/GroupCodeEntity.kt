package itmo.edugoolda.api.group.storage.entities

import itmo.edugoolda.api.group.storage.tables.GroupCodeTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GroupCodeEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, GroupCodeEntity>(GroupCodeTable)

    var code by GroupCodeTable.id
    val groupId by GroupCodeTable.groupId
    val createdAt by GroupCodeTable.createdAt

    var group by GroupEntity referencedOn GroupCodeTable.groupId
}