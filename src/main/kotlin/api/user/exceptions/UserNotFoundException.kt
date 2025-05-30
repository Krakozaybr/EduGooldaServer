package itmo.edugoolda.api.user.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException
import itmo.edugoolda.utils.EntityIdentifier

class UserNotFoundException(val userId: EntityIdentifier) : BaseException() {
    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(
                description = "User with id ${userId.stringValue} is not found",
                errorCode = "USER_NOT_FOUND"
            )
        )
    }
}
