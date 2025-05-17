package itmo.edugoolda.api.lessons.dto

import itmo.edugoolda.api.lessons.domain.SolutionMessageDomain
import itmo.edugoolda.api.user.dto.UserInfoDto
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolutionMessageDTO(
    @SerialName("id") val id: String,
    @SerialName("sent_at") val sentAt: Instant,
    @SerialName("message") val message: String,
    @SerialName("author") val author: UserInfoDto
)

fun SolutionMessageDomain.toDto() = SolutionMessageDTO(
    id = id.stringValue,
    sentAt = sentAt,
    message = message,
    author = UserInfoDto.from(author),
)
