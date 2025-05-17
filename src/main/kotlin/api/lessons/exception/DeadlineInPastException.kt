package itmo.edugoolda.api.lessons.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class DeadlineInPastException : BaseException() {

    companion object {
        const val CODE = "DEADLINE_IN_PAST"
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
