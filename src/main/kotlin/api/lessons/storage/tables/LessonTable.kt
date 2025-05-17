package itmo.edugoolda.api.lessons.storage.tables

import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.database.BaseTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object LessonTable : BaseTable("lessons") {
    val name = varchar("name", 300)
    val authorId = reference(
        name = "author",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val description = varchar("description", 3000).nullable()
    val isEstimatable = bool("is_estimatable").default(false)
    val deadline = timestamp("deadline").nullable()
    val opensAt = timestamp("opens_at").nullable()
}