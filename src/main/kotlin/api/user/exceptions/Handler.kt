package itmo.edugoolda.api.user.exceptions

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.plugins.StatusPagesHandler

object UserStatusPagesHandler : StatusPagesHandler {
    override fun StatusPagesConfig.configure() {
        exception<UserNotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    description = "User with id ${cause.userId.stringValue} is not found",
                    errorCode = "USER_NOT_FOUND"
                )
            )
        }
        exception<UserAlreadyDeletedException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    description = "User with id ${cause.userId.stringValue} has already been deleted",
                    errorCode = "ALREADY_DELETED"
                )
            )
        }
    }
}