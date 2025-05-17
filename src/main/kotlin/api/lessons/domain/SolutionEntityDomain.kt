package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.utils.EntityIdentifier

data class SolutionEntityDomain(
    val id: EntityIdentifier,
    val userId: EntityIdentifier,
    val teacherId: EntityIdentifier,
    val lessonId: EntityIdentifier,
    val status: SolutionStatus
)