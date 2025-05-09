package itmo.edugoolda.api.group.dto

import itmo.edugoolda.api.group.domain.model.GroupInfoDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupInfoDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("subject_name") val ownerName: String,
    @SerialName("owner_name") val subjectName: String,
) {
    companion object {
        fun from(groupInfoDomain: GroupInfoDomain) = GroupInfoDto(
            id = groupInfoDomain.id.stringValue,
            name = groupInfoDomain.name,
            ownerName = groupInfoDomain.ownerName,
            subjectName = groupInfoDomain.subjectName,
        )
    }
}