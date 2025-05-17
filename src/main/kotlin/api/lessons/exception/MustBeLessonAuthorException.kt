package itmo.edugoolda.api.lessons.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class MustBeLessonAuthorException : BaseException() {

    companion object {
        const val CODE = "FORBIDDEN"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.Forbidden,
            ErrorResponse(
                description = "Must be author of lesson",
                errorCode = CODE,
            )
        )
    }
}
