package itmo.edugoolda.api.user.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("role") val role: String
)
