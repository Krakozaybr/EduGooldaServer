package itmo.edugoolda.api.auth.route.v1

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.domain.AuthCredentials
import itmo.edugoolda.api.auth.domain.GetTokensUseCase
import itmo.edugoolda.api.auth.dto.AuthResponse
import itmo.edugoolda.api.auth.dto.LoginRequest
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.auth.storage.auth.AuthStorage
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import org.koin.core.Koin

fun Route.loginRoute(koin: Koin) {
    val authStorage = koin.get<AuthStorage>()
    val userStorage = koin.get<UserStorage>()
    val generateTokensUseCase = koin.get<GetTokensUseCase>()

    route("/login") {
        post<LoginRequest> {
            val userId = authStorage.checkCredentials(
                AuthCredentials.EmailPassword(
                    email = it.email,
                    password = it.password
                )
            ) ?: throw InvalidCredentialsException()

            val userRole = userStorage.getUserById(userId)?.role
                ?: throw UserNotFoundException(userId)

            val tokens = generateTokensUseCase.generateNew(userId)

            call.respond(
                HttpStatusCode.OK,
                AuthResponse(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                    userId = userId.stringValue,
                    role = userRole.data
                )
            )
        }
    }
}