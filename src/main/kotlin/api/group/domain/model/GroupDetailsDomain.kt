package itmo.edugoolda.api.group.domain.model

import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.LocalDateTime

data class GroupDetailsDomain(
    val id: EntityIdentifier,
    val name: String,
    val description: String?,
    val subjectDomain: SubjectDomain,
    val owner: UserInfoDomain,
    val studentsCount: Int,
    val requestsCount: Int,
    val bannedCount: Int,
    val newSolutionsCount: Int,
    val tasksCount: Int,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
)
