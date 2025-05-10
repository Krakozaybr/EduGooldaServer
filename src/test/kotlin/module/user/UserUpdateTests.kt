package module.user

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.auth.exception.InvalidBioException
import itmo.edugoolda.api.auth.exception.InvalidEmailException
import itmo.edugoolda.api.user.dto.UserInfoDto
import itmo.edugoolda.api.user.exceptions.EmailIsNotFreeException
import itmo.edugoolda.api.user.exceptions.InvalidUserNameException
import itmo.edugoolda.api.user.route.v1.UpdateProfileRequest
import module.*
import kotlin.test.Test
import kotlin.test.assertEquals

class UserUpdateTests : ModuleTest {

    @Test
    fun test_user_update_correct() = testJsonRequests { client ->

        val student = client.registerUniqueStudent()

        val expectedProfile = UserInfoDto(
            id = student.userId,
            name = "newName",
            email = "sndkfjskdf@email.com",
            role = "student",
            isDeleted = false,
            bio = "New bio"
        )

        client.sendRequest(
            url = "/api/v1/user/profile",
            body = UpdateProfileRequest(
                name = expectedProfile.name,
                email = expectedProfile.email,
                bio = expectedProfile.bio
            ),
            method = HttpMethod.Put,
            accessToken = student.accessToken
        ).expectOk()

        val actualProfile = client.sendRequest(
            url = "api/v1/user/${expectedProfile.id}",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).body<UserInfoDto>()

        assertEquals(
            expectedProfile,
            actualProfile
        )
    }

    @Test
    fun test_user_update_email_is_not_free() = testJsonRequests { client ->

        val student = client.registerUniqueStudent()
        val student2 = client.registerUniqueStudent()

        client.sendRequest(
            url = "/api/v1/user/profile",
            body = UpdateProfileRequest(
                name = faker.name.name(),
                email = student2.email,
                bio = faker.lorem.words()
            ),
            method = HttpMethod.Put,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.BadRequest, EmailIsNotFreeException.CODE)
    }

    @Test
    fun test_user_update_email_invalid_email() = testJsonRequests { client ->

        val student = client.registerUniqueStudent()

        client.sendRequest(
            url = "/api/v1/user/profile",
            body = UpdateProfileRequest(
                name = faker.name.name(),
                email = "sdfsdxvv",
                bio = faker.lorem.words()
            ),
            method = HttpMethod.Put,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.BadRequest, InvalidEmailException.CODE)
    }

    @Test
    fun test_user_update_email_invalid_name() = testJsonRequests { client ->

        val student = client.registerUniqueStudent()

        client.sendRequest(
            url = "/api/v1/user/profile",
            body = UpdateProfileRequest(
                name = "",
                email = student.email,
                bio = faker.lorem.words()
            ),
            method = HttpMethod.Put,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.BadRequest, InvalidUserNameException.CODE)
    }

    @Test
    fun test_user_update_email_invalid_bio() = testJsonRequests { client ->

        val student = client.registerUniqueStudent()

        client.sendRequest(
            url = "/api/v1/user/profile",
            body = UpdateProfileRequest(
                name = "sdfsdf",
                email = student.email,
                bio = faker.lorem.words().repeat(5001)
            ),
            method = HttpMethod.Put,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.BadRequest, InvalidBioException.CODE)
    }
}