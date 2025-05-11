package module.auth

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.auth.dto.AuthResponse
import itmo.edugoolda.api.auth.dto.LoginRequest
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import module.*
import kotlin.test.Test

class LoginTests : ModuleTest {

    @Test
    fun test_login_correct() = testJsonRequests { client ->
        val info = client.registerUniqueTeacher()

        val tokens = client.sendRequest(
            url = "/api/v1/auth/login",
            method = HttpMethod.Post,
            body = LoginRequest(
                email = info.email,
                password = info.password
            )
        ).expectOk().body<AuthResponse>()

        // Some auth requiring method
        client.sendRequest(
            url = "/api/v1/subjects",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        ).expectOk()
    }

    @Test
    fun test_login_not_valid_credentials() = testJsonRequests { client ->
        val info = client.registerUniqueTeacher()

        client.sendRequest(
            url = "/api/v1/auth/login",
            method = HttpMethod.Post,
            body = LoginRequest(
                email = info.email,
                password = "sdfjksdnf"
            )
        ).expectError(HttpStatusCode.Unauthorized, InvalidCredentialsException.CODE)
    }
}