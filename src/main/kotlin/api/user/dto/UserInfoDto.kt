package itmo.edugoolda.api.user.dto

import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.api.user.domain.UserRole.Companion.toDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("role") val role: String,
    @SerialName("is_deleted") val isDeleted: Boolean
) {
    companion object {
        fun from(info: UserInfoDomain) = UserInfoDto(
            id = info.id.value.toString(),
            name = info.name,
            email = info.email,
            role = info.role.toDTO(),
            isDeleted = info.isDeleted,
        )
    }
}
