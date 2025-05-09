package itmo.edugoolda.api.user.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.error.exceptions.IdFormatException
import itmo.edugoolda.api.user.dto.UserInfoDto
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.utils.EntityIdentifier
import org.koin.core.Koin

const val USER_ID_URL_PARAM = "user_id"

fun Route.detailsRoute(koin: Koin) {
    val userStorage = koin.get<UserStorage>()

    authenticate {
        get("/{$USER_ID_URL_PARAM}") {
            val userId = call.pathParameters[USER_ID_URL_PARAM]?.takeIf {
                it.isNotBlank()
            }?.let(EntityIdentifier::parse) ?: throw IdFormatException("user_id")

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            call.respond(
                HttpStatusCode.OK,
                UserInfoDto.from(user)
            )
        }
    }
}