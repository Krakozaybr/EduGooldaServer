package itmo.edugoolda.api.user.domain

data class UserInfo(
    val id: UserId,
    val email: String?,
    val name: String,
    val role: UserRole,
    val isDeleted: Boolean
)
