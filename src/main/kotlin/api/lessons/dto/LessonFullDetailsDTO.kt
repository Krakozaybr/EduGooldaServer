package itmo.edugoolda.api.lessons.dto

import itmo.edugoolda.api.group.dto.GroupInfoDto
import itmo.edugoolda.api.lessons.domain.LessonFullDetailsDomain
import itmo.edugoolda.api.user.dto.UserInfoDto
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonFullDetailsDTO(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("teacher") val teacher: UserInfoDto,
    @SerialName("deadline") val deadline: Instant?,
    @SerialName("opens_at") val opensAt: Instant?,
    @SerialName("groups") val groups: List<GroupInfoDto>,
    @SerialName("solutions_count") val solutionsCount: Int,
    @SerialName("is_estimatable") val isEstimatable: Boolean,
)

fun LessonFullDetailsDomain.toDto() = LessonFullDetailsDTO(
    id = id.stringValue,
    name = name,
    description = description,
    teacher = UserInfoDto.from(teacher),
    deadline = deadline,
    opensAt = opensAt,
    groups = groups.map { GroupInfoDto.from(it) },
    solutionsCount = solutionsCount,
    isEstimatable = isEstimatable
)
