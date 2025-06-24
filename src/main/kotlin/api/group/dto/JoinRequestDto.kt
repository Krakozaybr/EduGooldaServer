package itmo.edugoolda.api.group.dto

import itmo.edugoolda.api.group.domain.model.JoinRequestDomain
import itmo.edugoolda.api.user.dto.UserInfoDto
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JoinRequestDto(
    @SerialName("id") val id: String,
    @SerialName("sender") val sender: UserInfoDto,
    @SerialName("group") val group: GroupInfoDto,
    @SerialName("created_at") val createdAt: Instant
)

fun JoinRequestDomain.toDTO() = JoinRequestDto(
    id = id.value.toString(),
    sender = UserInfoDto.from(sender),
    group = GroupInfoDto.from(group),
    createdAt = createdAt,
)
