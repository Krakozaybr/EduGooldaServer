package itmo.edugoolda.api.auth.exception

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.plugins.StatusPagesHandler

object AuthStatusPagesHandler : StatusPagesHandler {
    override fun StatusPagesConfig.configure() {
        exception<InvalidEmailException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    errorCode = "EMAIL_INVALID",
                )
            )
        }
        exception<PasswordInvalidException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    errorCode = "PASSWORD_INVALID",
                )
            )
        }
        exception<UnknownUserRoleException> { call, cause ->
            call.respond(
                HttpStatusCode.PreconditionFailed,
                ErrorResponse(
                    errorCode = "UNKNOWN_USER_ROLE",
                    description = "Unknown user role: ${cause.value}"
                )
            )
        }
        exception<UserAlreadyExistsException> { call, _ ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    errorCode = "USER_EXIST",
                )
            )
        }
        exception<InvalidCredentialsException> { call, _ ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    errorCode = "INVALID_CREDENTIALS",
                )
            )
        }
    }
}