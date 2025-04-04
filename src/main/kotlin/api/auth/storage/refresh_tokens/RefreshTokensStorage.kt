package itmo.edugoolda.api.auth.storage.refresh_tokens

import itmo.edugoolda.api.user.domain.UserId
import kotlinx.datetime.LocalDateTime

interface RefreshTokensStorage {
    suspend fun removeToken(refreshToken: String)
    suspend fun putToken(refreshToken: String, userId: UserId, expiresAt: LocalDateTime)
    suspend fun getUserIdByRefreshToken(refreshToken: String): UserId?
}