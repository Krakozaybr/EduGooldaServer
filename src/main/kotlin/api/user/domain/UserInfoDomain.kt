package itmo.edugoolda.api.user.domain

import itmo.edugoolda.utils.EntityIdentifier

data class UserInfoDomain(
    val id: EntityIdentifier,
    val email: String,
    val name: String,
    val role: UserRole,
    val isDeleted: Boolean,
    val bio: String?
)
