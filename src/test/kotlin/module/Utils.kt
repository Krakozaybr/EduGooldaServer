package module

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import itmo.edugoolda.api.auth.dto.AuthResponse
import itmo.edugoolda.api.auth.dto.RegisterRequest
import itmo.edugoolda.api.error.ErrorResponse
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.assertEquals

val DefaultRegisterStudentRequest = RegisterRequest(
    email = "student@email.com",
    password = "SomePassword123",
    role = "student",
    name = "Kir"
)

val DefaultRegisterTeacherRequest = RegisterRequest(
    email = "teacher@email.com",
    password = "SomePassword123",
    role = "teacher",
    name = "Kir"
)

suspend fun HttpClient.registerStudent(
    request: RegisterRequest = DefaultRegisterStudentRequest
): AuthResponse {
    return post("/api/v1/auth/register") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }.expectOk().body<AuthResponse>()
}

suspend fun HttpClient.registerTeacher(
    request: RegisterRequest = DefaultRegisterTeacherRequest
): AuthResponse {
    return post("/api/v1/auth/register") {
        contentType(ContentType.Application.Json)
        setBody(request)
    }.expectOk().body<AuthResponse>()
}

fun testJsonRequests(
    block: suspend ApplicationTestBuilder.(client: HttpClient) -> Unit
) = testApplication {
    environment {
        config = ApplicationConfig("application.yaml")
    }
    val client = createClient {
        install(ContentNegotiation) {
            json()
        }
    }
    block(client)
}

fun removeDatabase() {
    Files.deleteIfExists(Path("database.db"))
}

suspend fun HttpResponse.expectError(code: HttpStatusCode, errorCode: String): ErrorResponse {
    val body = body<ErrorResponse>()
    assertEquals(
        code,
        this.status,
        message = body.toString()
    )
    assertEquals(
        errorCode,
        body.errorCode
    )
    return body
}

suspend fun HttpResponse.expectCode(code: HttpStatusCode): HttpResponse {
    if (code != this.status) {
        val body = body<ErrorResponse>()
        assertEquals(
            code,
            this.status,
            message = body.toString()
        )
    }
    return this
}

suspend fun HttpResponse.expectOk() = expectCode(HttpStatusCode.OK)

suspend fun HttpClient.sendRequest(
    url: String,
    method: HttpMethod,
    accessToken: String? = null
): HttpResponse {
    return request {
        this.method = method
        url(url)
        contentType(ContentType.Application.Json)
        accessToken?.let(::bearerAuth)
    }
}

suspend inline fun <reified T> HttpClient.sendRequest(
    url: String,
    method: HttpMethod,
    body: T?,
    accessToken: String? = null
): HttpResponse {
    return request {
        this.method = method
        url(url)
        contentType(ContentType.Application.Json)
        body?.let(::setBody)
        accessToken?.let(::bearerAuth)
    }
}
