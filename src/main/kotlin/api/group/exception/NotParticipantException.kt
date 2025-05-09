package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException
import itmo.edugoolda.utils.EntityIdentifier

class NotParticipantException(
    val studentId: EntityIdentifier,
    val groupId: EntityIdentifier
) : BaseException() {

    companion object {
        const val CODE = "NOT_PARTICIPANT"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "User with id=[${studentId.stringValue}] is not participant of group with id=[${groupId.stringValue}]",
                errorCode = CODE,
            )
        )
    }
}