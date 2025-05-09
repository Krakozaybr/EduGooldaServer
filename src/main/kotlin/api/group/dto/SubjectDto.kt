package itmo.edugoolda.api.group.dto

import itmo.edugoolda.api.group.domain.model.SubjectDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubjectDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String
) {
    companion object {
        fun from(subjectDomain: SubjectDomain) = SubjectDto(
            id = subjectDomain.id.stringValue,
            name = subjectDomain.name
        )
    }
}
