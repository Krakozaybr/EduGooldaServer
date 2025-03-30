package itmo.edugoolda.api.user.route.v1

import itmo.edugoolda.api.user.dto.UserDetailsResponse
import itmo.edugoolda.api.user.storage.UserStorage
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.Koin

const val USER_ID_URL_PARAM = "user_id"

fun Route.detailsRoute(koin: Koin) {
    val userStorage = koin.get<UserStorage>()

    authenticate {
        get("/{$USER_ID_URL_PARAM}") {
            val user = getUserOr404(userStorage) ?: return@get

            call.respond(
                HttpStatusCode.OK,
                UserDetailsResponse.from(user)
            )
        }
    }
}