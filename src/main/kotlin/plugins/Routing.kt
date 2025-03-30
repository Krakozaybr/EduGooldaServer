package itmo.edugoolda.plugins

import itmo.edugoolda.api.auth.configureAuthRouting
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.Koin

fun Application.configureRouting(koin: Koin) {
    install(Resources)
    routing {
        route("/api") {
            configureAuthRouting(koin)
        }
        get("/hello") {
            call.respondText("Hello, Ktor!")
        }
        authenticate {
            get("/hello_auth") {
                call.respondText("Hello, Ktor!")
            }
        }
    }
}
