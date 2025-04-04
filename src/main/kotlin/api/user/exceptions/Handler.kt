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
                    errorCode = "USER_NOT_FOUND"
                )
            )
        }
    }
}