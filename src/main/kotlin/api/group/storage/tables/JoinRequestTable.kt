package itmo.edugoolda.api.group.storage.tables

import itmo.edugoolda.api.group.domain.model.JoinRequestStatus
import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.database.BaseTable
import org.jetbrains.exposed.sql.ReferenceOption

object JoinRequestTable : BaseTable("join_requests") {
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
    val status = enumeration<JoinRequestStatus>("status")
}