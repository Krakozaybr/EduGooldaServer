package itmo.edugoolda.api.group.dto

import itmo.edugoolda.api.group.domain.model.GroupDetailsDomain
import itmo.edugoolda.api.user.dto.UserInfoDto
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("subject") val subject: SubjectDto,
    @SerialName("owner") val owner: UserInfoDto,
    @SerialName("students_count") val studentsCount: Int,
    @SerialName("requests_count") val requestsCount: Int,
    @SerialName("banned_count") val bannedCount: Int,
    @SerialName("new_solutions_count") val newSolutionsCount: Int,
    @SerialName("tasks_count") val tasksCount: Int,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("created_at") val createdAt: LocalDateTime,
) {
    companion object {
        fun from(details: GroupDetailsDomain) = GroupDetailsDto(
            id = details.id.stringValue,
            name = details.name,
            description = details.description,
            subject = SubjectDto.from(details.subjectDomain),
            owner = UserInfoDto.from(details.owner),
            studentsCount = details.studentsCount,
            requestsCount = details.requestsCount,
            bannedCount = details.bannedCount,
            newSolutionsCount = details.newSolutionsCount,
            tasksCount = details.tasksCount,
            isActive = details.isActive,
            createdAt = details.createdAt,
        )
    }
}
