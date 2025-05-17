package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.utils.EntityIdentifier

data class MessageEntityDomain(
    val id: EntityIdentifier,
    val text: String,
    val solutionId: EntityIdentifier,
    val authorId: EntityIdentifier,
)
