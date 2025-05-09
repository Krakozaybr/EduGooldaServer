package module.auth

import io.ktor.http.*
import itmo.edugoolda.api.auth.dto.LogoutRequest
import itmo.edugoolda.api.auth.dto.RefreshRequest
import module.ModuleTest
import module.registerStudent
import module.sendRequest
import module.testJsonRequests
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthTests : ModuleTest {
    @Test
    fun testLogout() = testJsonRequests { client ->
        val tokens = client.registerStudent()

        client.sendRequest(
            url = "/api/v1/auth/logout",
            body = LogoutRequest(tokens.refreshToken),
            method = HttpMethod.Post
        )

        assertEquals(
            HttpStatusCode.Unauthorized,
            client.sendRequest(
                url = "/api/v1/auth/refresh",
                body = RefreshRequest(tokens.refreshToken),
                method = HttpMethod.Post
            ).status
        )
    }
}
