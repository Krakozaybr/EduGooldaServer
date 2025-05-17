package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.api.group.domain.model.GroupInfoDomain
import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.Instant

data class LessonStudentDetailsDomain(
    val id: EntityIdentifier,
    val name: String,
    val description: String?,
    val teacher: UserInfoDomain,
    val deadline: Instant?,
    val groups: List<GroupInfoDomain>,
    val messages: List<SolutionMessageDomain>,
    val status: SolutionStatus,
    val isEstimatable: Boolean,
)
