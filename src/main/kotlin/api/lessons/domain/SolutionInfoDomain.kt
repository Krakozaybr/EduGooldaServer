package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.Instant

data class SolutionInfoDomain(
    val id: EntityIdentifier,
    val sentAt: Instant,
    val student: UserInfoDomain,
    val status: SolutionStatus,
    val lesson: LessonInfoDomain
)
