package itmo.edugoolda.api.auth.storage.auth

import itmo.edugoolda.api.auth.domain.AuthCredentials
import itmo.edugoolda.api.user.domain.UserId

interface AuthStorage {
    suspend fun saveCredentials(id: UserId, authCredentials: AuthCredentials)

    suspend fun checkCredentials(authCredentials: AuthCredentials): UserId?

    suspend fun getHashedPassword(userId: UserId): String?
}