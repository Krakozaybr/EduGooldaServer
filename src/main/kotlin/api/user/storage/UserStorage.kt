package itmo.edugoolda.api.user.storage

import itmo.edugoolda.api.user.domain.UserInfo
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.utils.EntityId

interface UserStorage {
    suspend fun createUser(
        email: String,
        name: String,
        role: UserRole
    ): EntityId

    suspend fun getUserData(userId: EntityId): UserInfo?

    suspend fun getUserByEmail(email: String): UserInfo?

    suspend fun getUserById(id: EntityId): UserInfo?

    suspend fun markDeleted(id: EntityId)
}
