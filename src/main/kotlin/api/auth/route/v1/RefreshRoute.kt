package itmo.edugoolda.api.auth.route.v1

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.domain.GetTokensUseCase
import itmo.edugoolda.api.auth.dto.RefreshRequest
import itmo.edugoolda.api.auth.dto.RefreshTokensResponse
import org.koin.core.Koin

fun Route.refreshRoute(koin: Koin) {
    val generateTokensUseCase = koin.get<GetTokensUseCase>()

    route("/refresh") {
        post<RefreshRequest> {
            val tokens = generateTokensUseCase.refreshTokens(it.refreshToken)

            call.respond(
                HttpStatusCode.OK,
                RefreshTokensResponse(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                )
            )
        }
    }
}