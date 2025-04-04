package itmo.edugoolda.api.auth

import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.route.v1.loginRoute
import itmo.edugoolda.api.auth.route.v1.refreshRoute
import itmo.edugoolda.api.auth.route.v1.registerRoute
import org.koin.core.Koin

fun Route.configureAuthRouting(koin: Koin) {

    // V1
    route("/v1/auth") {
        loginRoute(koin)
        registerRoute(koin)
        refreshRoute(koin)
    }
}