package itmo.edugoolda.api.group.storage.tables

import itmo.edugoolda.api.user.storage.tables.UserTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object GroupToUserTable : UUIDTable("groups_to_users") {
    val userId = reference(
        name = "user_id",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val groupId = reference(
        name = "group_id",
        refColumn = GroupTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    init {
        uniqueIndex(userId, groupId)
    }
}