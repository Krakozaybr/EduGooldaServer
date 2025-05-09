package itmo.edugoolda.api.error.exceptions

import io.ktor.server.application.*

abstract class BaseException : Exception() {
    abstract suspend fun handle(call: ApplicationCall)
}