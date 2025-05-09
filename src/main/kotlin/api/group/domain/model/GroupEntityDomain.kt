package itmo.edugoolda.api.group.domain.model

import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.LocalDateTime

data class GroupEntityDomain(
    val id: EntityIdentifier,
    val name: String,
    val description: String?,
    val ownerId: EntityIdentifier,
    val subjectId: EntityIdentifier,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
)
