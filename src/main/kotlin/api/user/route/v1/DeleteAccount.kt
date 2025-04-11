package itmo.edugoolda.api.user.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.user.exceptions.UserAlreadyDeletedException
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import org.koin.core.Koin

fun Route.deleteAccount(koin: Koin) {
    val userStorage = koin.get<UserStorage>()

    authenticate {
        delete("/") {
            val tokenContext = tokenContext ?: throw InvalidCredentialsException()
            val userId = tokenContext.userId

            val user = userStorage.getUserById(userId)

            user ?: throw UserNotFoundException(userId)

            if (user.isDeleted) throw UserAlreadyDeletedException(userId)

            userStorage.markDeleted(user.id)

            call.respond(HttpStatusCode.Accepted)
        }
    }
}