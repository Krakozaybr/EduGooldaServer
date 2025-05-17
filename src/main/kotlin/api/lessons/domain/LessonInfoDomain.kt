package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.LocalDateTime

data class LessonInfoDomain(
    val id: EntityIdentifier,
    val name: String,
    val description: String?,
    val teacher: UserInfoDomain,
    val createdAt: LocalDateTime
)
