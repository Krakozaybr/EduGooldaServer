package itmo.edugoolda.api.group.dto

import io.ktor.http.*
import itmo.edugoolda.api.group.utils.parsePagination

data class GroupsListParams(
    val query: String?,
    val subjectName: String?,
    val isFavourite: Boolean?,
    val paginationDto: PaginationDto
) {
    companion object {
        fun from(queryParams: Parameters) = GroupsListParams(
            query = queryParams["query"],
            subjectName = queryParams["subject_query"],
            isFavourite = queryParams["is_favourite"]?.toBooleanStrictOrNull(),
            paginationDto = queryParams.parsePagination(),
        )
    }
}
