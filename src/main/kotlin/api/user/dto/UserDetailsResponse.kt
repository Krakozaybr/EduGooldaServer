package itmo.edugoolda.api.user.dto

import itmo.edugoolda.api.user.domain.UserInfo
import itmo.edugoolda.api.user.domain.UserRole.Companion.toDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsResponse(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String?,
    @SerialName("role") val role: String,
    @SerialName("is_deleted") val isDeleted: Boolean
) {
    companion object {
        fun from(info: UserInfo) = UserDetailsResponse(
            id = info.id.stringValue,
            name = info.name,
            email = info.email,
            role = info.role.toDTO(),
            isDeleted = info.isDeleted,
        )
    }
}
