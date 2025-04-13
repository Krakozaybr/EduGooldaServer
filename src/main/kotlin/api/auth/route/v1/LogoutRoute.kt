package itmo.edugoolda.api.auth.route.v1

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.dto.LogoutRequest
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.auth.storage.refresh_tokens.RefreshTokensStorage
import org.koin.core.Koin

fun Route.logoutRoute(koin: Koin) {
    val refreshTokensStorage = koin.get<RefreshTokensStorage>()

    route("logout") {
        post<LogoutRequest> {
            val wasDeleted = refreshTokensStorage.removeToken(it.refreshToken) >= 1

            if (!wasDeleted) throw InvalidCredentialsException()

            call.respond(HttpStatusCode.OK)
        }
    }
}