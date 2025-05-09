package itmo.edugoolda.api.auth.storage.refresh_tokens

import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.LocalDateTime

interface RefreshTokensStorage {
    suspend fun removeToken(refreshToken: String): Int
    suspend fun putToken(refreshToken: String, userId: EntityIdentifier, expiresAt: LocalDateTime)
    suspend fun getEntityIdByRefreshTokenIfNotExpired(refreshToken: String): EntityIdentifier?
}