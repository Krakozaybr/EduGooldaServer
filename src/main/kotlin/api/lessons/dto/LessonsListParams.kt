package itmo.edugoolda.api.lessons.dto

import io.ktor.http.*
import itmo.edugoolda.api.error.exceptions.IdFormatException
import itmo.edugoolda.api.group.dto.PaginationDto
import itmo.edugoolda.api.group.utils.parsePagination
import itmo.edugoolda.utils.EntityIdentifier

data class LessonsListParams(
    val query: String?,
    val groupId: EntityIdentifier?,
    val paginationDto: PaginationDto
) {
    companion object {
        fun from(queryParams: Parameters) = LessonsListParams(
            query = queryParams["query"],
            groupId = run {
                val groupId = queryParams["group_id"] ?: return@run null
                EntityIdentifier.parse(groupId) ?: throw IdFormatException("group_id")
            },
            paginationDto = queryParams.parsePagination(),
        )
    }
}
