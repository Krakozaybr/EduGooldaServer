package itmo.edugoolda.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import org.koin.core.Koin

fun interface StatusPagesHandler {
    fun StatusPagesConfig.configure()
}

fun Application.configureStatusPages(koin: Koin) {
    install(StatusPages) {
        koin.getAll<StatusPagesHandler>().forEach {
            with(it) {
                configure()
            }
        }
    }
}