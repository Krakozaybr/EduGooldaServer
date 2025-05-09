package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException
import itmo.edugoolda.utils.EntityIdentifier

class UserIsAlreadyParticipantException(
    val userId: EntityIdentifier,
    val groupId: EntityIdentifier
) : BaseException() {
    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "User with id=${userId.stringValue} is already participant of group with id=${groupId.stringValue}",
                errorCode = "ALREADY_PARTICIPANT "
            )
        )
    }
}