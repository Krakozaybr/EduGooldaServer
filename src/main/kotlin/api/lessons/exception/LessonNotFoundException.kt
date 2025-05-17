package itmo.edugoolda.api.lessons.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException
import itmo.edugoolda.utils.EntityIdentifier

class LessonNotFoundException(val id: EntityIdentifier) : BaseException() {

    companion object {
        const val CODE = "LESSON_NOT_FOUND"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(
                description = "Lesson with id=${id.stringValue} is not found",
                errorCode = CODE,
            )
        )
    }
}
