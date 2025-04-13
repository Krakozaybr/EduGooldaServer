package itmo.edugoolda.api.user.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.error.exceptions.QueryIdFormatException
import itmo.edugoolda.api.user.dto.UserDetailsResponse
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.utils.EntityId
import org.koin.core.Koin

const val USER_ID_URL_PARAM = "user_id"

fun Route.detailsRoute(koin: Koin) {
    val userStorage = koin.get<UserStorage>()

    authenticate {
        get("/{$USER_ID_URL_PARAM}") {
            val userId = call.pathParameters[USER_ID_URL_PARAM]?.takeIf {
                it.isNotBlank()
            }?.let(EntityId::parse) ?: throw QueryIdFormatException()

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            call.respond(
                HttpStatusCode.OK,
                UserDetailsResponse.from(user)
            )
        }
    }
}