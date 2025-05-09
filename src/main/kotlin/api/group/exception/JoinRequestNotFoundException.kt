package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException
import itmo.edugoolda.utils.EntityIdentifier

class JoinRequestNotFoundException(val id: EntityIdentifier) : BaseException() {
    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Request with id=${id.stringValue} is not found",
                errorCode = "NOT_FOUND"
            )
        )
    }
}