package itmo.edugoolda.api.auth.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class InvalidBioException : BaseException() {

    companion object {
        const val CODE = "BIO_INVALID"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Bio length cannot be greater than 5000 chars",
                errorCode = CODE,
            )
        )
    }
}
