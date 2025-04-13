package itmo.edugoolda.api.error.exceptions

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.plugins.StatusPagesHandler

object ErrorStatusPagesHandler : StatusPagesHandler {
    override fun StatusPagesConfig.configure() {
        exception<DataFormatException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    description = cause.description,
                    errorCode = "DATA_FORMAT_EXCEPTION"
                )
            )
        }
    }
}