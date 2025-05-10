package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class MustBeSubjectOwnerException : BaseException() {

    companion object {
        const val CODE = "MUST_BE_SUBJECT_OWNER"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.Forbidden,
            ErrorResponse(
                description = "User must be owner of subject",
                errorCode = CODE,
            )
        )
    }
}
