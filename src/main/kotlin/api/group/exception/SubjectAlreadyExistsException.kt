package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class SubjectAlreadyExistsException(val name: String) : BaseException() {

    companion object {
        const val CODE = "SUBJECT_ALREADY_EXISTS"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Subject with name=${name} already exists",
                errorCode = CODE,
            )
        )
    }
}
