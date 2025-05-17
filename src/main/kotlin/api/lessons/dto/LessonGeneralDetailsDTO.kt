package itmo.edugoolda.api.lessons.dto

import itmo.edugoolda.api.group.dto.GroupInfoDto
import itmo.edugoolda.api.lessons.domain.LessonGeneralDetailsDomain
import itmo.edugoolda.api.user.dto.UserInfoDto
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonGeneralDetailsDTO(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("teacher") val teacher: UserInfoDto,
    @SerialName("deadline") val deadline: Instant?,
    @SerialName("groups") val groups: List<GroupInfoDto>,
    @SerialName("is_estimatable") val isEstimatable: Boolean,
)

fun LessonGeneralDetailsDomain.toDto() = LessonGeneralDetailsDTO(
    id = id.stringValue,
    name = name,
    description = description,
    teacher = UserInfoDto.from(teacher),
    deadline = deadline,
    groups = groups.map { GroupInfoDto.from(it) },
    isEstimatable = isEstimatable
)
