package itmo.edugoolda.api.lessons.dto

import io.ktor.http.*
import itmo.edugoolda.api.error.exceptions.IdFormatException
import itmo.edugoolda.api.error.exceptions.PrintableEnum
import itmo.edugoolda.api.group.dto.PaginationDto
import itmo.edugoolda.api.group.utils.parsePagination
import itmo.edugoolda.api.lessons.domain.SolutionStatus
import itmo.edugoolda.utils.EntityIdentifier

data class SolutionsListParams(
    val groupId: EntityIdentifier?,
    val lessonId: EntityIdentifier?,
    val status: SolutionStatus?,
    val paginationDto: PaginationDto
) {
    companion object {
        fun from(queryParams: Parameters) = SolutionsListParams(
            groupId = run {
                val groupId = queryParams["group_id"] ?: return@run null
                EntityIdentifier.parse(groupId) ?: throw IdFormatException("group_id")
            },
            lessonId = run {
                val lessonId = queryParams["lesson_id"] ?: return@run null
                EntityIdentifier.parse(lessonId) ?: throw IdFormatException("lesson_id")
            },
            status = queryParams["status"]?.let(PrintableEnum::parseOrThrow),
            paginationDto = queryParams.parsePagination(),
        )
    }
}
