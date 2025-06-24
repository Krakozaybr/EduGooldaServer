package itmo.edugoolda.api.group.domain.model

import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.Instant

enum class JoinRequestStatus {
    Pending,
    Declined,
    Accepted,
    Cancelled
}

data class JoinRequestDomain(
    val id: EntityIdentifier,
    val sender: UserInfoDomain,
    val group: GroupInfoDomain,
    val createdAt: Instant
)

data class JoinRequestInfoDomain(
    val id: EntityIdentifier,
    val senderId: EntityIdentifier,
    val groupIdentifier: EntityIdentifier,
    val status: JoinRequestStatus
)
