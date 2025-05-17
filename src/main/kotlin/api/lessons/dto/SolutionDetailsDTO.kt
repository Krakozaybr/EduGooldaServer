package itmo.edugoolda.api.lessons.dto

import itmo.edugoolda.api.lessons.domain.SolutionDetailsDomain
import itmo.edugoolda.api.user.dto.UserInfoDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolutionDetailsDTO(
    @SerialName("id") val id: String,
    @SerialName("lesson") val lesson: LessonGeneralDetailsDTO,
    @SerialName("messages") val messages: List<SolutionMessageDTO>,
    @SerialName("status") val status: String,
    @SerialName("author") val author: UserInfoDto,
)

fun SolutionDetailsDomain.toDto() = SolutionDetailsDTO(
    id = id.stringValue,
    lesson = lesson.toDto(),
    messages = messages.map { it.toDto() },
    status = status.string,
    author = UserInfoDto.from(author)
)
