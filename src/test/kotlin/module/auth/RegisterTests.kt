package module.auth

import io.ktor.http.*
import itmo.edugoolda.api.auth.dto.RegisterRequest
import itmo.edugoolda.api.auth.exception.InvalidEmailException
import itmo.edugoolda.api.auth.exception.PasswordInvalidException
import itmo.edugoolda.api.auth.exception.UserAlreadyExistsException
import itmo.edugoolda.api.user.domain.UserRole
import module.*
import kotlin.test.Test

class RegisterTests : ModuleTest {

    @Test
    fun test_register_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        // Some auth requiring method
        client.sendRequest(
            url = "/api/v1/subjects",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        ).expectOk()
    }

    @Test
    fun test_register_user_already_exists() = testJsonRequests { client ->

        val email = faker.internet.email()
        val password = "SecurePassword123"

        client.sendRequest(
            url = "/api/v1/auth/register",
            method = HttpMethod.Post,
            body = RegisterRequest(
                email = email,
                password = password,
                name = faker.name.name(),
                role = UserRole.Student.data
            )
        ).expectOk()

        client.sendRequest(
            url = "/api/v1/auth/register",
            method = HttpMethod.Post,
            body = RegisterRequest(
                email = email,
                password = password,
                name = faker.name.name(),
                role = UserRole.Student.data
            )
        ).expectError(HttpStatusCode.BadRequest, UserAlreadyExistsException.CODE)
    }

    @Test
    fun test_register_password_empty() = testJsonRequests { client ->

        val email = faker.internet.email()
        val password = ""

        client.sendRequest(
            url = "/api/v1/auth/register",
            method = HttpMethod.Post,
            body = RegisterRequest(
                email = email,
                password = password,
                name = faker.name.name(),
                role = UserRole.Student.data
            )
        ).expectError(HttpStatusCode.BadRequest, PasswordInvalidException.CODE)
    }

    @Test
    fun test_register_password_without_digit() = testJsonRequests { client ->

        val email = faker.internet.email()
        val password = "SEsfdfds"

        client.sendRequest(
            url = "/api/v1/auth/register",
            method = HttpMethod.Post,
            body = RegisterRequest(
                email = email,
                password = password,
                name = faker.name.name(),
                role = UserRole.Student.data
            )
        ).expectError(HttpStatusCode.BadRequest, PasswordInvalidException.CODE)
    }

    @Test
    fun test_register_password_too_long() = testJsonRequests { client ->

        val email = faker.internet.email()
        val password = "sdf".repeat(132)

        client.sendRequest(
            url = "/api/v1/auth/register",
            method = HttpMethod.Post,
            body = RegisterRequest(
                email = email,
                password = password,
                name = faker.name.name(),
                role = UserRole.Student.data
            )
        ).expectError(HttpStatusCode.BadRequest, PasswordInvalidException.CODE)
    }

    @Test
    fun test_register_password_without_lowercase_letter() = testJsonRequests { client ->

        val email = faker.internet.email()
        val password = "SFSDFSDF798"

        client.sendRequest(
            url = "/api/v1/auth/register",
            method = HttpMethod.Post,
            body = RegisterRequest(
                email = email,
                password = password,
                name = faker.name.name(),
                role = UserRole.Student.data
            )
        ).expectError(HttpStatusCode.BadRequest, PasswordInvalidException.CODE)
    }

    @Test
    fun test_register_password_without_uppercase_letter() = testJsonRequests { client ->

        val email = faker.internet.email()
        val password = "sdfsdfsdf7897"

        client.sendRequest(
            url = "/api/v1/auth/register",
            method = HttpMethod.Post,
            body = RegisterRequest(
                email = email,
                password = password,
                name = faker.name.name(),
                role = UserRole.Student.data
            )
        ).expectError(HttpStatusCode.BadRequest, PasswordInvalidException.CODE)
    }

    @Test
    fun test_register_password_email_wrong() = testJsonRequests { client ->

        val email = "askfsdf"
        val password = "SecurePassword123"

        client.sendRequest(
            url = "/api/v1/auth/register",
            method = HttpMethod.Post,
            body = RegisterRequest(
                email = email,
                password = password,
                name = faker.name.name(),
                role = UserRole.Student.data
            )
        ).expectError(HttpStatusCode.BadRequest, InvalidEmailException.CODE)
    }
}