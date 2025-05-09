package itmo.edugoolda.api.auth.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class UnknownUserRoleException(val value: String) : BaseException() {
    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.PreconditionFailed,
            ErrorResponse(
                errorCode = "UNKNOWN_USER_ROLE",
                description = "Unknown user role: $value"
            )
        )
    }
}