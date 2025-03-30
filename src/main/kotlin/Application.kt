package itmo.edugoolda

import itmo.edugoolda.api.auth.storage.AuthStorage
import itmo.edugoolda.api.auth.storage.DatabaseAuthStorage
import itmo.edugoolda.api.user.storage.DatabaseUserStorage
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.configureDatabase
import itmo.edugoolda.plugins.configureRouting
import itmo.edugoolda.plugins.configureSecurity
import itmo.edugoolda.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun main(args: Array<String>) {
    EngineMain.main(args)
}

val mainModule = module {
    singleOf(::DatabaseUserStorage) bind UserStorage::class
    singleOf(::DatabaseAuthStorage) bind AuthStorage::class
}

fun Application.module() {
    val koin = startKoin {
        modules(mainModule)
    }.koin

    val logger = environment.log
    val jwtService = configureSecurity(environment.config)

    koin.declare(jwtService)
    koin.declare(logger)

    configureSerialization()
    configureDatabase(environment.config)
    configureRouting(koin)
}
