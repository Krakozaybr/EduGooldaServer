package itmo.edugoolda.api.group.utils

import io.ktor.http.*
import itmo.edugoolda.api.group.dto.PaginationDto
import itmo.edugoolda.api.group.exception.QueryParamsException

fun Parameters.parsePagination(): PaginationDto {
    val page = get("page")?.toIntOrNull()
        ?: throw QueryParamsException()

    if (page <= 0) {
        throw QueryParamsException("Page must be positive integer")
    }

    val pageSize = get("page_size")?.toIntOrNull()
        ?: throw QueryParamsException()

    if (pageSize <= 0) {
        throw QueryParamsException("Page size must be positive integer")
    }

    return PaginationDto(
        page = page,
        pageSize = pageSize
    )
}
