package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.api.group.domain.model.GroupInfoDomain
import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.Instant

data class LessonFullDetailsDomain(
    val id: EntityIdentifier,
    val name: String,
    val description: String?,
    val teacher: UserInfoDomain,
    val deadline: Instant?,
    val opensAt: Instant?,
    val groups: List<GroupInfoDomain>,
    val solutionsCount: Int,
    val isEstimatable: Boolean,
)
