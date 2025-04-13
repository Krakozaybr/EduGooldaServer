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
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.test.assertEquals

val DefaultRegisterRequest = RegisterRequest(
    email = "some@email.com",
    password = "SomePassword123",
    role = "student",
    name = "Kir"
)

suspend fun HttpClient.register(): AuthResponse {
    return post("/api/v1/auth/register") {
        contentType(ContentType.Application.Json)
        setBody(DefaultRegisterRequest)
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

suspend fun HttpResponse.expectOk(): HttpResponse {
    assertEquals(
        HttpStatusCode.OK,
        status,
        message = "Request ${request.url} failed: ${bodyAsText()}"
    )
    return this
}
