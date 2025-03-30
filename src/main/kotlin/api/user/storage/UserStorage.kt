package itmo.edugoolda.api.user.storage

import itmo.edugoolda.api.user.domain.UserId
import itmo.edugoolda.api.user.domain.UserInfo
import itmo.edugoolda.api.user.domain.UserRole

interface UserStorage {
    suspend fun createUser(
        email: String,
        name: String,
        role: UserRole
    ): UserId

    suspend fun getUserData(userId: UserId): UserInfo?

    suspend fun getUserByEmail(email: String): UserInfo?

    suspend fun getUserById(id: UserId): UserInfo?

    suspend fun markDeleted(id: UserId)
}
