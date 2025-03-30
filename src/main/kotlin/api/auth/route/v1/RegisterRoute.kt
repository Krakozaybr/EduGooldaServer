package itmo.edugoolda.api.auth.route.v1

import itmo.edugoolda.api.auth.domain.AuthCredentials
import itmo.edugoolda.api.auth.dto.AuthResponse
import itmo.edugoolda.api.auth.dto.RegisterRequest
import itmo.edugoolda.api.auth.service.JwtService
import itmo.edugoolda.api.auth.storage.AuthStorage
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.utils.validateEmail
import itmo.edugoolda.utils.validatePassword
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.Koin

fun Route.registerRoute(koin: Koin) {
    val jwtService = koin.get<JwtService>()
    val authStorage = koin.get<AuthStorage>()
    val userStorage = koin.get<UserStorage>()

    post<RegisterRequest>("register") {
        val role = UserRole.fromString(it.role)

        if (role == null) {
            call.respond(
                HttpStatusCode.PreconditionFailed,
                ErrorResponse(
                    errorCode = "UNKNOWN_USER_ROLE",
                )
            )
            return@post
        }

        if (!validateEmail(it.email)) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    errorCode = "EMAIL_INVALID",
                )
            )
            return@post
        }

        if (userStorage.getUserByEmail(it.email) != null) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    errorCode = "USER_EXIST",
                )
            )
            return@post
        }

        if (!validatePassword(it.password)) {
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    errorCode = "PASSWORD_INVALID",
                )
            )
            return@post
        }

        val userId = userStorage.createUser(
            email = it.email,
            name = it.name,
            role = role
        )

        authStorage.saveCredentials(
            id = userId,
            authCredentials = AuthCredentials.EmailPassword(
                email = it.email,
                password = it.password
            )
        )

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