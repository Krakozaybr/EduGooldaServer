package itmo.edugoolda.api.group.storage.tables

import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.database.BaseTable
import org.jetbrains.exposed.sql.ReferenceOption

object SubjectTable : BaseTable("subjects") {
    val name = varchar("name", 300).index()
    val ownerId = reference(
        name = "owner_id",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}