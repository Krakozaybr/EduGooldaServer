package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException
import itmo.edugoolda.utils.EntityIdentifier

class SubjectNotFoundException(val subjectId: EntityIdentifier) : BaseException() {

    companion object {
        const val CODE = "SUBJECT_NOT_FOUND"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(
                description = "Subject with id=${subjectId.stringValue} is not found",
                errorCode = CODE,
            )
        )
    }
}
