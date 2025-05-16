package itmo.edugoolda.api.group.dto

import itmo.edugoolda.api.group.domain.model.GroupInfoDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupInfoDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("owner_name") val ownerName: String,
    @SerialName("subject_name") val subjectName: String,
    @SerialName("is_favourite") val isFavourite: Boolean,
) {
    companion object {
        fun from(groupInfoDomain: GroupInfoDomain) = GroupInfoDto(
            id = groupInfoDomain.id.stringValue,
            name = groupInfoDomain.name,
            ownerName = groupInfoDomain.ownerName,
            subjectName = groupInfoDomain.subjectName,
            isFavourite = groupInfoDomain.isFavourite
        )
    }
}