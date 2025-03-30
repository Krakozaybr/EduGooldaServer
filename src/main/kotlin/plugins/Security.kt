package itmo.edugoolda.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import itmo.edugoolda.api.auth.service.JwtService
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.user.domain.UserId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureSecurity(
    config: ApplicationConfig
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
                val payload = credential.payload
                val tokenType = payload.getClaim(JwtService.TOKEN_TYPE_KEY).asString()
                val userId = payload.getClaim(JwtService.USER_ID_KEY).asString()

                if (tokenType == JwtService.ACCESS_TOKEN_TYPE && userId != null) {
                    JWTPrincipal(payload)
                } else {
                    null
                }
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

val RoutingContext.currentUserId: UserId? get() {
    return call.principal<JWTPrincipal>()?.payload
        ?.getClaim(JwtService.USER_ID_KEY)
        ?.asString()
        ?.let(UserId::parse)
}
