package itmo.edugoolda.api.auth.storage.auth

import itmo.edugoolda.api.auth.domain.AuthCredentials
import itmo.edugoolda.utils.EntityIdentifier

interface AuthStorage {
    suspend fun saveCredentials(id: EntityIdentifier, authCredentials: AuthCredentials)

    suspend fun checkCredentials(authCredentials: AuthCredentials): EntityIdentifier?

    suspend fun getHashedPassword(userId: EntityIdentifier): String?
}