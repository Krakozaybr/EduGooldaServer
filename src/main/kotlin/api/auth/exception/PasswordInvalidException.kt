package itmo.edugoolda.api.auth.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class PasswordInvalidException : BaseException() {
    companion object {
        const val CODE = "PASSWORD_INVALID"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Password must have length from 6 to 32, have at least one digit, one uppercase and lowercase letters",
                errorCode = CODE,
            )
        )
    }
}
