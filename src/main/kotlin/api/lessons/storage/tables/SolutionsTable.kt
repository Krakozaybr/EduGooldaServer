package itmo.edugoolda.api.lessons.storage.tables

import itmo.edugoolda.api.lessons.domain.SolutionStatus
import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.database.BaseTable
import org.jetbrains.exposed.sql.ReferenceOption

object SolutionsTable : BaseTable("solutions") {
    val userId = reference(
        name = "user_id",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val lessonId = reference(
        name = "lesson_id",
        refColumn = LessonTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val teacherId = reference(
        name = "teacher_id",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val status = enumeration<SolutionStatus>("status")
        .default(SolutionStatus.Pending)

    init {
        uniqueIndex(userId, lessonId)
    }
}