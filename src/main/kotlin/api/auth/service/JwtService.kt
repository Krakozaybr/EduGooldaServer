package itmo.edugoolda.api.auth.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import itmo.edugoolda.api.user.domain.UserId
import io.ktor.util.logging.*
import java.util.*
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
    }

    fun createAccessToken(userId: UserId) = generateToken(
        data = mapOf(
            TOKEN_TYPE_KEY to ACCESS_TOKEN_TYPE,
            USER_ID_KEY to userId.stringValue
        ),
        expirationTime = accessTokenExpiration
    )

    fun createRefreshToken(userId: UserId) = generateToken(
        data = mapOf(
            TOKEN_TYPE_KEY to REFRESH_TOKEN_TYPE,
            USER_ID_KEY to userId.stringValue
        ),
        expirationTime = refreshTokenExpiration
    )

    private fun generateToken(
        data: Map<String, String>,
        expirationTime: Duration
    ) = JWT
        .create()
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .withExpiresAt(Date(System.currentTimeMillis() + expirationTime.inWholeMilliseconds))
        .withPayload(data)
        .sign(algorithm)
}