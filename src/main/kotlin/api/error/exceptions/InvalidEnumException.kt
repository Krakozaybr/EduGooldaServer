package itmo.edugoolda.api.error.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse

interface PrintableEnum {
    val string: String

    companion object {
        inline fun <reified T> parseOrNull(value: String): T? where T : PrintableEnum, T : Enum<*> {
            return T::class.java.enumConstants.firstOrNull {
                it.string == value
            }
        }

        inline fun <reified T> parseOrThrow(value: String): T where T : PrintableEnum, T : Enum<*> {
            return parseOrNull(value) ?: throw InvalidEnumException(T::class.java.enumConstants.toList())
        }
    }
}

class InvalidEnumException(val entries: Iterable<PrintableEnum>) : BaseException() {
    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.BadRequest,
            ErrorResponse(
                description = "Value must be one of following: ${entries.joinToString { it.string }}",
                errorCode = "DATA_FORMAT_EXCEPTION"
            )
        )
    }
}
