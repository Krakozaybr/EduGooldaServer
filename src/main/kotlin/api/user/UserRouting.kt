package itmo.edugoolda.api.user

import io.ktor.server.routing.*
import itmo.edugoolda.api.user.route.v1.deleteAccount
import itmo.edugoolda.api.user.route.v1.detailsRoute
import org.koin.core.Koin

fun Route.configureUserRouting(koin: Koin) {

    // V1
    route("/v1/user") {
        detailsRoute(koin)
        deleteAccount(koin)
    }
}