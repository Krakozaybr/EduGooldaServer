package itmo.edugoolda.api.user.exceptions

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.error.exceptions.BaseException
import itmo.edugoolda.api.user.domain.UserRole

class UnsuitableUserRoleException(vararg val possibleRoles: UserRole) : BaseException() {

    companion object {
        const val CODE = "UNSUITABLE_ROLE"
    }

    override suspend fun handle(call: ApplicationCall) {
        call.respond(
            HttpStatusCode.Forbidden,
            ErrorResponse(
                description = "User must have one of following roles: $possibleRoles",
                errorCode = CODE
            )
        )
    }
}
