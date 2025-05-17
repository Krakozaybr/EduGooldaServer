package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.Instant

data class LessonEntityDomain(
    val id: EntityIdentifier,
    val name: String,
    val authorId: EntityIdentifier,
    val description: String?,
    val isEstimatable: Boolean,
    val deadline: Instant?,
    val opensAt: Instant?,
)
