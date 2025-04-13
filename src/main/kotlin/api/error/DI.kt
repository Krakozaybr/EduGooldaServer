package itmo.edugoolda.api.error

import itmo.edugoolda.api.error.exceptions.ErrorStatusPagesHandler
import itmo.edugoolda.plugins.StatusPagesHandler
import org.koin.dsl.bind
import org.koin.dsl.module

val errorModule = module {
    single { ErrorStatusPagesHandler } bind StatusPagesHandler::class
}