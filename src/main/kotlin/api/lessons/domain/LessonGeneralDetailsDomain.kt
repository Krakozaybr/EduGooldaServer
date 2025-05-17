package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.api.group.domain.model.GroupInfoDomain
import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.Instant

data class LessonGeneralDetailsDomain(
    val id: EntityIdentifier,
    val name: String,
    val description: String?,
    val teacher: UserInfoDomain,
    val deadline: Instant?,
    val groups: List<GroupInfoDomain>,
    val isEstimatable: Boolean,
)
