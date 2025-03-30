package itmo.edugoolda.api.auth.route.v1

import itmo.edugoolda.api.auth.domain.AuthCredentials
import itmo.edugoolda.api.auth.dto.AuthResponse
import itmo.edugoolda.api.auth.dto.LoginRequest
import itmo.edugoolda.api.auth.service.JwtService
import itmo.edugoolda.api.auth.storage.AuthStorage
import itmo.edugoolda.api.error.ErrorResponse
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.Koin

fun Route.loginRoute(koin: Koin) {
    val jwtService = koin.get<JwtService>()
    val authStorage = koin.get<AuthStorage>()

    route("login") {
        post<LoginRequest> {
            val userId = authStorage.checkCredentials(
                AuthCredentials.EmailPassword(
                    email = it.email,
                    password = it.password
                )
            )

            if (userId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        errorCode = "INVALID_CREDENTIALS",
                    )
                )
                return@post
            }

            val refreshToken = jwtService.createRefreshToken(userId)
            val accessToken = jwtService.createAccessToken(userId)

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    userId = userId.stringValue
                )
            )
        }
    }
}