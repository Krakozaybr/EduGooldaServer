package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException
import itmo.edugoolda.utils.EntityIdentifier

class UserIsBannedException(
    val userId: EntityIdentifier,
    val groupId: EntityIdentifier
) : BaseException() {

    companion object {
        const val CODE = "USER_IS_BANNED"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.Forbidden,
            ErrorResponse(
                description = "User with id=${userId.stringValue} is banned in group with id=${groupId.stringValue}",
                errorCode = CODE
            )
        )
    }
}