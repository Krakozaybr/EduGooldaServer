package itmo.edugoolda.api.lessons.dto

import itmo.edugoolda.api.group.dto.GroupInfoDto
import itmo.edugoolda.api.lessons.domain.LessonStudentDetailsDomain
import itmo.edugoolda.api.user.dto.UserInfoDto
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonStudentDetailsDTO(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("teacher") val teacher: UserInfoDto,
    @SerialName("deadline") val deadline: Instant?,
    @SerialName("groups") val groups: List<GroupInfoDto>,
    @SerialName("messages") val messages: List<SolutionMessageDTO>,
    @SerialName("status") val status: String,
    @SerialName("is_estimatable") val isEstimatable: Boolean,
)

fun LessonStudentDetailsDomain.toDto() = LessonStudentDetailsDTO(
    id = id.stringValue,
    name = name,
    description = description,
    teacher = UserInfoDto.from(teacher),
    deadline = deadline,
    groups = groups.map { GroupInfoDto.from(it) },
    messages = messages.map { it.toDto() },
    status = status.string,
    isEstimatable = isEstimatable
)
