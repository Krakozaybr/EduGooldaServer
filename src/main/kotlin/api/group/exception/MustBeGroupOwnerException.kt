package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class MustBeGroupOwnerException : BaseException() {

    companion object {
        const val CODE = "FORBIDDEN"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.Forbidden,
            ErrorResponse(
                description = "User must be owner of group",
                errorCode = CODE,
            )
        )
    }
}
