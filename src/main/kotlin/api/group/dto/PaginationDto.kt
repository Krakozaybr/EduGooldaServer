package itmo.edugoolda.api.group.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaginationDto(
    @SerialName("page_size") val pageSize: Int,
    @SerialName("page") val page: Int,
) {
    val skip get() = (page - 1) * pageSize
}
