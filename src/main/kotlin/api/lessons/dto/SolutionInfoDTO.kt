package itmo.edugoolda.api.lessons.dto

import itmo.edugoolda.api.lessons.domain.SolutionInfoDomain
import itmo.edugoolda.api.user.dto.UserInfoDto
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolutionInfoDTO(
    @SerialName("id") val id: String,
    @SerialName("sent_at") val sentAt: Instant,
    @SerialName("student") val student: UserInfoDto,
    @SerialName("status") val status: String,
    @SerialName("lesson_info") val lessonInfo: LessonInfoDTO
)

fun SolutionInfoDomain.toDto() = SolutionInfoDTO(
    id = id.stringValue,
    sentAt = sentAt,
    student = UserInfoDto.from(student),
    status = status.string,
    lessonInfo = lesson.toDto()
)
