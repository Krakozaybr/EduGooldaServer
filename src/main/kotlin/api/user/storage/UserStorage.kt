package itmo.edugoolda.api.user.storage

import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.utils.EntityIdentifier

interface UserStorage {
    suspend fun createUser(
        email: String,
        name: String,
        role: UserRole
    ): EntityIdentifier

    suspend fun updateUser(
        id: EntityIdentifier,
        email: String,
        name: String,
        bio: String?
    )

    suspend fun getUserByEmail(email: String): UserInfoDomain?

    suspend fun getUserById(id: EntityIdentifier): UserInfoDomain?

    suspend fun markDeleted(id: EntityIdentifier)
}
