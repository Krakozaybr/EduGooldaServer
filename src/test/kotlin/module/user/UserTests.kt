package module.user

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.user.dto.UserInfoDto
import itmo.edugoolda.api.user.route.v1.UpdateProfileRequest
import module.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UserTests : ModuleTest {
    @Test
    fun testUserUpdate() = testJsonRequests { client ->
        val tokens = client.registerStudent()

        val expectedProfile = UserInfoDto(
            id = tokens.userId,
            name = "newName",
            email = "sndkfjskdf@email.com",
            role = "student",
            isDeleted = false
        )

        client.sendRequest(
            url = "/api/v1/user/profile",
            body = UpdateProfileRequest(
                name = expectedProfile.name,
                email = expectedProfile.email
            ),
            method = HttpMethod.Put,
            accessToken = tokens.accessToken
        ).expectOk()

        val actualProfile = client.sendRequest(
            url = "api/v1/user/${expectedProfile.id}",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        ).body<UserInfoDto>()

        assertEquals(
            expectedProfile,
            actualProfile
        )
    }
}