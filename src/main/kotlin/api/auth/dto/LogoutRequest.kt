package itmo.edugoolda.api.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(
    @SerialName("refresh_token") val refreshToken: String
)