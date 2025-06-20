package itmo.edugoolda.api.auth.route.v1

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.domain.AuthCredentials
import itmo.edugoolda.api.auth.domain.GetTokensUseCase
import itmo.edugoolda.api.auth.dto.AuthResponse
import itmo.edugoolda.api.auth.dto.RegisterRequest
import itmo.edugoolda.api.auth.exception.InvalidEmailException
import itmo.edugoolda.api.auth.exception.PasswordInvalidException
import itmo.edugoolda.api.auth.exception.UnknownUserRoleException
import itmo.edugoolda.api.auth.exception.UserAlreadyExistsException
import itmo.edugoolda.api.auth.storage.auth.AuthStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.InvalidUserNameException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.utils.validateEmail
import itmo.edugoolda.utils.validateName
import itmo.edugoolda.utils.validatePassword
import org.koin.core.Koin

fun Route.registerRoute(koin: Koin) {
    val authStorage = koin.get<AuthStorage>()
    val userStorage = koin.get<UserStorage>()
    val generateTokensUseCase = koin.get<GetTokensUseCase>()

    post<RegisterRequest>("register") {
        val role = UserRole.fromString(it.role) ?: throw UnknownUserRoleException(it.role)

        if (!validateEmail(it.email)) throw InvalidEmailException()

        if (userStorage.getUserByEmail(it.email) != null) throw UserAlreadyExistsException()

        if (!validatePassword(it.password)) throw PasswordInvalidException()

        if (!validateName(it.name)) throw InvalidUserNameException()

        if (userStorage.getUserByEmail(it.email) != null) {
            throw UserAlreadyExistsException()
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

        val tokens = generateTokensUseCase.generateNew(userId)

        call.respond(
            HttpStatusCode.OK,
            AuthResponse(
                accessToken = tokens.accessToken,
                refreshToken = tokens.refreshToken,
                userId = userId.stringValue,
                role = it.role
            )
        )
    }
}