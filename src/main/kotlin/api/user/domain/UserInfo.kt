package itmo.edugoolda.api.user.domain

import itmo.edugoolda.utils.EntityId

data class UserInfo(
    val id: EntityId,
    val email: String?,
    val name: String,
    val role: UserRole,
    val isDeleted: Boolean
)
