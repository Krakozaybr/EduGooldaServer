package itmo.edugoolda.api.lessons.storage.entities

import itmo.edugoolda.api.group.storage.entities.toGroupInfoDomain
import itmo.edugoolda.api.lessons.domain.LessonStudentDetailsDomain
import itmo.edugoolda.api.lessons.domain.SolutionDetailsDomain
import itmo.edugoolda.api.lessons.domain.SolutionEntityDomain
import itmo.edugoolda.api.lessons.domain.SolutionInfoDomain
import itmo.edugoolda.api.lessons.storage.tables.MessagesTable
import itmo.edugoolda.api.lessons.storage.tables.SolutionsTable
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.api.user.storage.entities.toDomain
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class SolutionEntity(id: EntityID<UUID>) : BaseEntity(id, SolutionsTable) {
    companion object : BaseEntityClass<SolutionEntity>(SolutionsTable)

    var userId by SolutionsTable.userId
    var lessonId by SolutionsTable.lessonId
    var teacherId by SolutionsTable.teacherId
    var status by SolutionsTable.status

    var user by UserEntity referencedOn SolutionsTable.userId
    var lesson by LessonEntity referencedOn SolutionsTable.lessonId
    var teacher by UserEntity referencedOn SolutionsTable.teacherId
    val messages by MessageEntity referrersOn MessagesTable.solutionId
}

fun SolutionEntity.toSolutionDetailsDomain() = SolutionDetailsDomain(
    id = EntityIdentifier.parse(id.value),
    lesson = lesson.toLessonGeneralDetailsDomain(),
    messages = messages.map { it.toSolutionMessageDomain() },
    status = status,
    author = user.toDomain()
)

fun SolutionEntity.toSolutionEntityDomain() = SolutionEntityDomain(
    id = EntityIdentifier.parse(id.value),
    userId = EntityIdentifier.parse(userId.value),
    teacherId = EntityIdentifier.parse(teacherId.value),
    lessonId = EntityIdentifier.parse(lessonId.value),
    status = status,
)

fun SolutionEntity.toSolutionInfoDomain() = SolutionInfoDomain(
    id = EntityIdentifier.parse(id.value),
    sentAt = createdAt.toInstant(TimeZone.UTC),
    student = user.toDomain(),
    status = status,
    lesson = lesson.toLessonInfoDomain()
)

fun SolutionEntity.toLessonStudentDetailsDomain() = LessonStudentDetailsDomain(
    id = EntityIdentifier.parse(id.value),
    name = lesson.name,
    description = lesson.description,
    teacher = teacher.toDomain(),
    deadline = lesson.deadline,
    groups = lesson.groups.map { it.toGroupInfoDomain() },
    messages = messages.map { it.toSolutionMessageDomain() },
    status = status,
    isEstimatable = lesson.isEstimatable,
)
