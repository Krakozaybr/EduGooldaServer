package itmo.edugoolda.api.user.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.auth.exception.InvalidEmailException
import itmo.edugoolda.api.auth.exception.UnknownUserRoleException
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.dto.UpdateProfileRequest
import itmo.edugoolda.api.user.dto.UserDetailsResponse
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.validateEmail
import org.koin.core.Koin

fun Route.updateRoute(koin: Koin) {
    val userStorage = koin.get<UserStorage>()

    authenticate {
        put<UpdateProfileRequest>("/profile") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            val role = UserRole.fromString(it.role) ?: throw UnknownUserRoleException(it.role)

            if (!validateEmail(it.email)) throw InvalidEmailException()

            userStorage.updateUser(
                email = it.email,
                name = it.name,
                role = role
            )

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            call.respond(
                HttpStatusCode.OK,
                UserDetailsResponse.from(user)
            )
        }
    }
}