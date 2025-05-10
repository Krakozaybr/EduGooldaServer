package itmo.edugoolda.api.group.domain.model

import itmo.edugoolda.utils.EntityIdentifier

data class GroupInfoDomain(
    val id: EntityIdentifier,
    val name: String,
    val ownerName: String,
    val subjectName: String,
    val isFavourite: Boolean
)