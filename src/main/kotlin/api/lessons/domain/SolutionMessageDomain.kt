package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.Instant

data class SolutionMessageDomain(
    val id: EntityIdentifier,
    val sentAt: Instant,
    val message: String,
    val author: UserInfoDomain
)
