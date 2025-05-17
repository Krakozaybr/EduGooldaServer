package itmo.edugoolda.api.lessons.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class LessonDescriptionException : BaseException() {

    companion object {
        const val CODE = "INVALID_METADATA"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Description length must be in interval of 1 and 3000",
                errorCode = CODE,
            )
        )
    }
}
