package itmo.edugoolda.api.auth.domain

import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.auth.service.JwtService
import itmo.edugoolda.api.auth.storage.auth.AuthStorage
import itmo.edugoolda.api.auth.storage.refresh_tokens.RefreshTokensStorage
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.utils.EntityId

class GetTokensUseCase(
    private val jwtService: JwtService,
    private val authStorage: AuthStorage,
    private val refreshTokensStorage: RefreshTokensStorage
) {
    suspend fun generateNew(userId: EntityId): AuthTokens {
        val passwordHash = authStorage.getHashedPassword(userId)
            ?: throw UserNotFoundException(userId)

        return generateTokens(userId, passwordHash)
    }

    suspend fun refreshTokens(refreshToken: String): AuthTokens {
        val userId = refreshTokensStorage.getEntityIdByRefreshTokenIfNotExpired(refreshToken)

        refreshTokensStorage.removeToken(refreshToken)

        userId ?: throw InvalidCredentialsException()

        val passwordHash = authStorage.getHashedPassword(userId)
            ?: throw UserNotFoundException(userId)

        return generateTokens(userId, passwordHash)
    }

    private suspend fun generateTokens(
        userId: EntityId,
        passwordHash: String
    ): AuthTokens {
        val newRefreshToken = jwtService.createRefreshToken(userId)
        val newAccessToken = jwtService.createAccessToken(userId, passwordHash)

        refreshTokensStorage.putToken(
            userId = userId,
            refreshToken = newRefreshToken.token,
            expiresAt = newRefreshToken.expiresAt
        )

        return AuthTokens(
            accessToken = newAccessToken.token,
            refreshToken = newRefreshToken.token
        )
    }
}