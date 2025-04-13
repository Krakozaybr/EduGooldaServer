package itmo.edugoolda.api.auth.storage.refresh_tokens

import itmo.edugoolda.utils.EntityId
import kotlinx.datetime.LocalDateTime

interface RefreshTokensStorage {
    suspend fun removeToken(refreshToken: String): Int
    suspend fun putToken(refreshToken: String, userId: EntityId, expiresAt: LocalDateTime)
    suspend fun getEntityIdByRefreshTokenIfNotExpired(refreshToken: String): EntityId?
}