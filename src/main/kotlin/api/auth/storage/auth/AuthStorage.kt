package itmo.edugoolda.api.auth.storage.auth

import itmo.edugoolda.api.auth.domain.AuthCredentials
import itmo.edugoolda.utils.EntityId

interface AuthStorage {
    suspend fun saveCredentials(id: EntityId, authCredentials: AuthCredentials)

    suspend fun checkCredentials(authCredentials: AuthCredentials): EntityId?

    suspend fun getHashedPassword(userId: EntityId): String?
}