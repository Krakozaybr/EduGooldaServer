package module.auth

import io.ktor.http.*
import itmo.edugoolda.api.auth.dto.LogoutRequest
import itmo.edugoolda.api.auth.dto.RefreshRequest
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import module.*
import kotlin.test.Test

class LogoutTests : ModuleTest {

    @Test
    fun test_logout_correct() = testJsonRequests { client ->
        val info = client.registerUniqueTeacher()

        client.sendRequest(
            url = "/api/v1/auth/logout",
            method = HttpMethod.Post,
            body = LogoutRequest(
                refreshToken = info.refreshToken
            )
        )

        client.sendRequest(
            url = "/api/v1/auth/refresh",
            method = HttpMethod.Post,
            body = RefreshRequest(
                refreshToken = info.refreshToken
            )
        ).expectError(HttpStatusCode.Unauthorized, InvalidCredentialsException.CODE)
    }
}