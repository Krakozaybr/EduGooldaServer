package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier

data class SolutionDetailsDomain(
    val id: EntityIdentifier,
    val lesson: LessonGeneralDetailsDomain,
    val messages: List<SolutionMessageDomain>,
    val status: SolutionStatus,
    val author: UserInfoDomain
)
