package itmo.edugoolda.api.user.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.auth.exception.InvalidEmailException
import itmo.edugoolda.api.user.dto.UserInfoDto
import itmo.edugoolda.api.user.exceptions.EmailIsNotFreeException
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.validateEmail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.updateRoute(koin: Koin) {
    val userStorage = koin.get<UserStorage>()

    authenticate {
        put<UpdateProfileRequest>("/profile") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val actualUser = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            if (!validateEmail(it.email)) throw InvalidEmailException()

            if (userStorage.getUserByEmail(it.email)?.id !in listOf(null, actualUser.id)) {
                throw EmailIsNotFreeException(it.email)
            }

            userStorage.updateUser(
                id = userId,
                email = it.email,
                name = it.name
            )

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            call.respond(
                HttpStatusCode.OK,
                UserInfoDto.from(user)
            )
        }
    }
}

@Serializable
data class UpdateProfileRequest(
    @SerialName("name") val name: String,
    @SerialName("email") val email: String
)