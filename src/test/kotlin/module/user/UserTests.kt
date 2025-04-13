package module.user

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import itmo.edugoolda.api.user.dto.UpdateProfileRequest
import itmo.edugoolda.api.user.dto.UserDetailsResponse
import module.ModuleTest
import module.expectOk
import module.register
import module.testJsonRequests
import kotlin.test.Test
import kotlin.test.assertEquals

class UserTests : ModuleTest {
    @Test
    fun testUserUpdate() = testJsonRequests { client ->
        val tokens = client.register()

        val expectedProfile = UserDetailsResponse(
            id = tokens.userId,
            name = "newName",
            email = "sndkfjskdf@email.com",
            role = "teacher",
            isDeleted = false
        )

        client.put("/api/v1/user/profile") {
            contentType(ContentType.Application.Json)
            bearerAuth(tokens.accessToken)
            setBody(
                UpdateProfileRequest(
                    name = expectedProfile.name,
                    email = expectedProfile.email,
                    role = expectedProfile.role,
                )
            )
        }.expectOk()

        val actualProfile = client.get("api/v1/user/${expectedProfile.id}") {
            contentType(ContentType.Application.Json)
            bearerAuth(tokens.accessToken)
        }.body<UserDetailsResponse>()

        assertEquals(
            expectedProfile,
            actualProfile
        )
    }
}