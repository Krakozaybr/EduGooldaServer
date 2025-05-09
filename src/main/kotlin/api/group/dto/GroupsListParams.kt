package itmo.edugoolda.api.group.dto

import io.ktor.http.*
import itmo.edugoolda.api.group.utils.parsePagination

data class GroupsListParams(
    val query: String?,
    val subjectId: String?,
    val paginationDto: PaginationDto
) {
    companion object {
        fun from(queryParams: Parameters) = GroupsListParams(
            query = queryParams["query"],
            subjectId = queryParams["subject_id"],
            paginationDto = queryParams.parsePagination()
        )
    }
}
