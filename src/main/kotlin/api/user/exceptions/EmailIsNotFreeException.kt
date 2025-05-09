package itmo.edugoolda.api.user.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class EmailIsNotFreeException(val email: String) : BaseException() {

    companion object {
        const val CODE = "EMAIL_IS_NOT_FREE"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Email: $email is not free",
                errorCode = CODE
            )
        )
    }
}
