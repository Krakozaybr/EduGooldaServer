package itmo.edugoolda.api.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("error_code") val errorCode: String,
    @SerialName("description") val description: String? = null
)
