package itmo.edugoolda.api.auth.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class InvalidCredentialsException : BaseException() {
    companion object {
        const val CODE = "INVALID_CREDENTIALS"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.Unauthorized,
            ErrorResponse(
                errorCode = CODE,
            )
        )
    }
}
