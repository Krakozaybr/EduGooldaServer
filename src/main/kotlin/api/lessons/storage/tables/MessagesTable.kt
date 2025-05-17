package itmo.edugoolda.api.lessons.storage.tables

import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.database.BaseTable
import org.jetbrains.exposed.sql.ReferenceOption

object MessagesTable : BaseTable("messages") {
    val text = varchar("message", 30000)
    val solutionId = reference(
        name = "solution_id",
        refColumn = SolutionsTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val authorId = reference(
        name = "author",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
}