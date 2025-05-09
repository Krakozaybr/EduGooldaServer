package itmo.edugoolda.api.error.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse

open class DataFormatException(val description: String) : BaseException() {
    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = description,
                errorCode = "DATA_FORMAT_EXCEPTION"
            )
        )
    }
}
