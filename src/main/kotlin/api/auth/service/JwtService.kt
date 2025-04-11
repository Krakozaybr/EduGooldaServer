package itmo.edugoolda.api.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.util.logging.*
import itmo.edugoolda.utils.EntityId
import kotlinx.datetime.*
import kotlin.time.Duration

class JwtService(
    private val logger: Logger,
    private val jwtAudience: String,
    private val jwtDomain: String,
    private val accessTokenExpiration: Duration,
    private val refreshTokenExpiration: Duration,
    private val algorithm: Algorithm
) {
    companion object {
        const val TOKEN_TYPE_KEY = "token_type"
        const val ACCESS_TOKEN_TYPE = "ACCESS_TOKEN_TYPE"
        const val REFRESH_TOKEN_TYPE = "REFRESH_TOKEN_TYPE"

        const val USER_ID_KEY = "user_id"
        const val PASSWORD_HASH_KEY = "password_hash"
    }

    data class TokenInfo(
        val token: String,
        val expiresAt: LocalDateTime
    )

    fun createAccessToken(
        userId: EntityId,
        passwordHash: String
    ) = generateToken(
        data = mapOf(
            TOKEN_TYPE_KEY to ACCESS_TOKEN_TYPE,
            USER_ID_KEY to userId.stringValue,
            PASSWORD_HASH_KEY to passwordHash
        ),
        expirationTime = accessTokenExpiration
    )

    fun createRefreshToken(userId: EntityId) = generateToken(
        data = mapOf(
            TOKEN_TYPE_KEY to REFRESH_TOKEN_TYPE,
            USER_ID_KEY to userId.stringValue
        ),
        expirationTime = refreshTokenExpiration
    )

    private fun generateToken(
        data: Map<String, String>,
        expirationTime: Duration
    ): TokenInfo {
        val timeZone = TimeZone.currentSystemDefault()
        val expiresAt = Clock.System.now()
            .plus(expirationTime)
            .toLocalDateTime(timeZone)

        val token = JWT
            .create()
            .withAudience(jwtAudience)
            .withIssuer(jwtDomain)
            .withExpiresAt(expiresAt.toInstant(timeZone).toJavaInstant())
            .withPayload(data)
            .sign(algorithm)

        return TokenInfo(token, expiresAt)
    }
}