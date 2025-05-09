package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException
import itmo.edugoolda.utils.EntityIdentifier

class NotBannedException(val studentId: EntityIdentifier) : BaseException() {
    companion object {
        const val CODE = "NOT_BANNED"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(
                description = "User with id=[${studentId.stringValue}] is not banned",
                errorCode = CODE,
            )
        )
    }
}
