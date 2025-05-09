package itmo.edugoolda.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.service.JwtService
import itmo.edugoolda.api.auth.storage.auth.AuthStorage
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.utils.EntityIdentifier
import org.koin.core.Koin
import kotlin.time.Duration.Companion.seconds

fun Application.configureSecurity(
    config: ApplicationConfig,
    koin: Koin
): JwtService {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = config.property("jwt.audience").getString()
    val jwtDomain = config.property("jwt.domain").getString()
    val jwtRealm = config.property("jwt.realm").getString()
    val jwtSecret = config.property("jwt.secret").getString()

    val (
        accessTokenExpiration,
        refreshTokenExpiration,
    ) = listOf(
        "accessTokenExpiration",
        "refreshTokenExpiration",
    ).map {
        environment.config.property("jwt.$it").getString().toLong().seconds
    }

    authentication {
        jwt {
            realm = jwtRealm

            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )

            validate { credential ->
                val authStorage = koin.get<AuthStorage>()

                val token = credential.payload

                val tokenType = token.getClaim(JwtService.TOKEN_TYPE_KEY).asString()

                if (tokenType != JwtService.ACCESS_TOKEN_TYPE) return@validate null

                val userId = token.getClaim(JwtService.USER_ID_KEY)
                    .asString()
                    ?.let(EntityIdentifier::parse)
                    ?: return@validate null

                val password = token.getClaim(JwtService.PASSWORD_HASH_KEY).asString()
                val hashedPassword = authStorage.getHashedPassword(userId)

                if (password != hashedPassword) return@validate null

                JWTPrincipal(token)
            }

            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    ErrorResponse(
                        errorCode = "InvalidAccessToken",
                        description = "The access token is invalid or expired"
                    )
                )
            }
        }
    }

    return JwtService(
        logger = environment.log,
        jwtAudience = jwtAudience,
        jwtDomain = jwtDomain,
        algorithm = Algorithm.HMAC256(jwtSecret),
        accessTokenExpiration = accessTokenExpiration,
        refreshTokenExpiration = refreshTokenExpiration,
    )
}

data class TokenContext(
    val userId: EntityIdentifier,
    val passwordHash: String,
)

val RoutingContext.tokenContext: TokenContext?
    get() {
        val payload = call.principal<JWTPrincipal>()?.payload
            ?: return null

        val passwordHash = payload.getClaim(JwtService.PASSWORD_HASH_KEY)?.asString() ?: return null
        val userId = payload.getClaim(JwtService.USER_ID_KEY)?.asString()?.let(EntityIdentifier::parse)
            ?: return null

        return TokenContext(
            passwordHash = passwordHash,
            userId = userId
        )
    }
