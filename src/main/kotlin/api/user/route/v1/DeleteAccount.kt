package itmo.edugoolda.api.user.route.v1

import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.currentUserId
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.Koin

fun Route.deleteAccount(koin: Koin) {
    val userStorage = koin.get<UserStorage>()

    delete("/") {
        val userId = currentUserId

        if (userId == null) {
            call.respond(HttpStatusCode.NotFound)
            return@delete
        }

        val user = userStorage.getUserById(userId)

        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
            return@delete
        }

        if (user.isDeleted) {
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    errorCode = "ALREADY_DELETED"
                )
            )
            return@delete
        }

        userStorage.markDeleted(user.id)

        call.respond(HttpStatusCode.Accepted)
    }
}