package itmo.edugoolda.api.lessons.dto

import itmo.edugoolda.api.lessons.domain.LessonInfoDomain
import itmo.edugoolda.api.user.dto.UserInfoDto
import itmo.edugoolda.utils.defaultFormat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonInfoDTO(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("teacher") val teacher: UserInfoDto,
    @SerialName("createdAt") val createdAt: String
)

fun LessonInfoDomain.toDto() = LessonInfoDTO(
    id = id.stringValue,
    name = name,
    description = description,
    teacher = UserInfoDto.from(teacher),
    createdAt = createdAt.defaultFormat(),
)
