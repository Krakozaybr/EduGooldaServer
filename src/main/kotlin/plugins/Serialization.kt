package itmo.edugoolda.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val defaultJson = Json {
    ignoreUnknownKeys = true
    allowTrailingComma = true
    explicitNulls = true
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(defaultJson)
    }
}