package module.auth

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.auth.dto.LogoutRequest
import itmo.edugoolda.api.auth.dto.RefreshRequest
import itmo.edugoolda.api.auth.dto.RefreshTokensResponse
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import module.*
import kotlin.test.Test

class RefreshTests : ModuleTest {
    @Test
    fun test_refresh_correct() = testJsonRequests { client ->
        val info = client.registerUniqueTeacher()

        val newTokens = client.sendRequest(
            url = "/api/v1/auth/refresh",
            method = HttpMethod.Post,
            body = RefreshRequest(
                refreshToken = info.refreshToken
            )
        ).expectOk().body<RefreshTokensResponse>()

        client.sendRequest(
            url = "/api/v1/auth/logout",
            method = HttpMethod.Post,
            body = LogoutRequest(
                refreshToken = info.refreshToken
            )
        ).expectError(HttpStatusCode.Unauthorized, InvalidCredentialsException.CODE)

        client.sendRequest(
            url = "/api/v1/auth/logout",
            method = HttpMethod.Post,
            body = LogoutRequest(
                refreshToken = newTokens.refreshToken
            )
        ).expectOk()
    }
}