package itmo.edugoolda

import io.ktor.server.application.*
import io.ktor.server.netty.*
import itmo.edugoolda.api.auth.authModule
import itmo.edugoolda.api.error.errorModule
import itmo.edugoolda.api.group.groupModule
import itmo.edugoolda.api.user.userModule
import itmo.edugoolda.plugins.*
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun main(args: Array<String>) {
    EngineMain.main(args)
}

val mainModule = module {
    includes(
        authModule,
        userModule,
        errorModule,
        groupModule
    )
}

fun Application.module() {
    val koin = startKoin {
        modules(mainModule)
    }.koin

    val logger = environment.log
    val jwtService = configureSecurity(environment.config, koin)

    koin.declare(jwtService)
    koin.declare(logger)

    configureSerialization()
    configureDatabase(environment.config, koin)
    configureRouting(koin)
    configureStatusPages(koin)
}
