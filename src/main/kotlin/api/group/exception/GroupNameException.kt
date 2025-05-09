package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class GroupNameException : BaseException() {

    companion object {
        const val CODE = "INVALID_METADATA"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Name length must be in interval of 1 and 300, not blank",
                errorCode = CODE,
            )
        )
    }
}
