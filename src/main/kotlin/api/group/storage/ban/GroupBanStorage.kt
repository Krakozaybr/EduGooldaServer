package itmo.edugoolda.api.group.storage.ban

import itmo.edugoolda.utils.EntityIdentifier

interface GroupBanStorage {
    suspend fun banUser(groupId: EntityIdentifier, userId: EntityIdentifier)

    suspend fun unbanUser(groupId: EntityIdentifier, userId: EntityIdentifier): Boolean
}