package itmo.edugoolda.api.lessons.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class OpensAfterDeadlineException : BaseException() {

    companion object {
        const val CODE = "OPENS_AFTER_DEADLINE"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                errorCode = CODE,
            )
        )
    }
}
