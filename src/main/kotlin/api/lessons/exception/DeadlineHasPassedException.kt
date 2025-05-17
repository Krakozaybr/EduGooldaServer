package itmo.edugoolda.api.lessons.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class DeadlineHasPassedException : BaseException() {

    companion object {
        const val CODE = "DEADLINE_HAS_PASSED"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.Forbidden,
            ErrorResponse(
                errorCode = CODE,
            )
        )
    }
}
