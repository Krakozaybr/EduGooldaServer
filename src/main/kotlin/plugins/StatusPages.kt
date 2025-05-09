package itmo.edugoolda.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import itmo.edugoolda.api.error.exceptions.BaseException
import org.koin.core.Koin

fun interface StatusPagesHandler {
    fun StatusPagesConfig.configure()
}

fun Application.configureStatusPages(koin: Koin) {
    install(StatusPages) {
        exception<BaseException> { call, cause ->
            cause.handle(call)
        }
    }
}