package itmo.edugoolda.api.user.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class InvalidUserNameException : BaseException() {

    companion object {
        const val CODE = "NAME_INVALID"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Name must be not blank and its length must be lower or equal than 300 chars",
                errorCode = CODE
            )
        )
    }
}
