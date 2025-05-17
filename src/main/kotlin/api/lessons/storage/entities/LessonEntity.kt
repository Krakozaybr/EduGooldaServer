package itmo.edugoolda.api.lessons.storage.entities

import itmo.edugoolda.api.group.storage.entities.GroupEntity
import itmo.edugoolda.api.group.storage.entities.toGroupInfoDomain
import itmo.edugoolda.api.lessons.domain.LessonEntityDomain
import itmo.edugoolda.api.lessons.domain.LessonFullDetailsDomain
import itmo.edugoolda.api.lessons.domain.LessonGeneralDetailsDomain
import itmo.edugoolda.api.lessons.domain.LessonInfoDomain
import itmo.edugoolda.api.lessons.storage.tables.GroupToLessonTable
import itmo.edugoolda.api.lessons.storage.tables.LessonTable
import itmo.edugoolda.api.lessons.storage.tables.SolutionsTable
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.api.user.storage.entities.toDomain
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class LessonEntity(id: EntityID<UUID>) : BaseEntity(id, LessonTable) {
    companion object : BaseEntityClass<LessonEntity>(LessonTable)

    var name by LessonTable.name
    var authorId by LessonTable.authorId
    var description by LessonTable.description
    var isEstimatable by LessonTable.isEstimatable
    var deadline by LessonTable.deadline
    var opensAt by LessonTable.opensAt

    var author by UserEntity referencedOn LessonTable.authorId
    val solutions by SolutionEntity referrersOn SolutionsTable.lessonId
    val groups by GroupEntity via GroupToLessonTable
}

fun LessonEntity.toLessonEntityDomain() = LessonEntityDomain(
    id = EntityIdentifier.parse(id.value),
    name = name,
    authorId = EntityIdentifier.parse(authorId.value),
    description = description,
    isEstimatable = isEstimatable,
    deadline = deadline,
    opensAt = opensAt,
)

fun LessonEntity.toLessonFullDetailsDomain() = LessonFullDetailsDomain(
    id = EntityIdentifier.parse(id.value),
    name = name,
    description = description,
    teacher = author.toDomain(),
    deadline = deadline,
    opensAt = opensAt,
    groups = groups.map { it.toGroupInfoDomain() },
    solutionsCount = solutions.count().toInt(),
    isEstimatable = isEstimatable,
)

fun LessonEntity.toLessonGeneralDetailsDomain() = LessonGeneralDetailsDomain(
    id = EntityIdentifier.parse(id.value),
    name = name,
    description = description,
    teacher = author.toDomain(),
    deadline = deadline,
    groups = groups.map { it.toGroupInfoDomain() },
    isEstimatable = isEstimatable,
)

fun LessonEntity.toLessonInfoDomain() = LessonInfoDomain(
    id = EntityIdentifier.parse(id.value),
    name = name,
    description = description,
    teacher = author.toDomain(),
    createdAt = createdAt,
)
