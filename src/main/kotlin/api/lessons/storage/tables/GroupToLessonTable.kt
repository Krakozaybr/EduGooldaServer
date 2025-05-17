package itmo.edugoolda.api.lessons.storage.tables

import itmo.edugoolda.api.group.storage.tables.GroupTable
import itmo.edugoolda.utils.database.BaseTable
import org.jetbrains.exposed.sql.ReferenceOption

object GroupToLessonTable : BaseTable("groups_to_lessons") {
    val groupId = reference(
        name = "group_id",
        refColumn = GroupTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val lessonId = reference(
        name = "lesson_id",
        refColumn = LessonTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    init {
        uniqueIndex(groupId, lessonId)
    }
}