package itmo.edugoolda.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.configureAuthRouting
import itmo.edugoolda.api.backup.configureBackupRouting
import itmo.edugoolda.api.group.configureGroupRouting
import itmo.edugoolda.api.user.configureUserRouting
import org.koin.core.Koin

fun Application.configureRouting(koin: Koin) {
    routing {
        route("/api") {
            configureAuthRouting(koin)
            configureUserRouting(koin)
            configureBackupRouting(koin)
            configureGroupRouting(koin)
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
