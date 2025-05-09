package itmo.edugoolda.api.group.exception

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException

class QueryParamsException(val description: String? = null) : BaseException() {
    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = description ?: "Error in query params. Check contracts",
                errorCode = "QUERY_PARAMS_ERROR",
            )
        )
    }
}
