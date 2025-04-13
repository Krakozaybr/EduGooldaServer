package module.auth

import io.ktor.client.request.*
import io.ktor.http.*
import itmo.edugoolda.api.auth.dto.LogoutRequest
import itmo.edugoolda.api.auth.dto.RefreshRequest
import module.ModuleTest
import module.register
import module.testJsonRequests
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthTests : ModuleTest {
    @Test
    fun testLogout() = testJsonRequests { client ->
        val tokens = client.register()

        client.post("/api/v1/auth/logout") {
            contentType(ContentType.Application.Json)
            setBody(LogoutRequest(tokens.refreshToken))
        }

        assertEquals(
            HttpStatusCode.Unauthorized,
            client.post("/api/v1/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshRequest(tokens.refreshToken))
            }.status
        )
    }
}
