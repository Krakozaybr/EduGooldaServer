package itmo.edugoolda.api.lessons.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class GroupsErrorException : BaseException() {
    companion object {
        const val CODE = "GROUPS_ERROR"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Some groups are dublicated or not exist",
                errorCode = CODE,
            )
        )
    }
}