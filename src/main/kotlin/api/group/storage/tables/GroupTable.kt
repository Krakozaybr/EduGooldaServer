package itmo.edugoolda.api.group.storage.tables

import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.database.BaseTable
import org.jetbrains.exposed.sql.ReferenceOption

object GroupTable : BaseTable("groups") {
    val name = varchar("name", 300)
    val description = varchar("description", 3000).nullable()
    val ownerId = reference(
        name = "ownerId",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val subjectId = reference(
        name = "subjectId",
        refColumn = SubjectTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    val isActive = bool("is_active").default(true)
}