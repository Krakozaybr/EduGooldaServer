package itmo.edugoolda.api.auth

import itmo.edugoolda.api.auth.domain.GetTokensUseCase
import itmo.edugoolda.api.auth.storage.auth.AuthStorage
import itmo.edugoolda.api.auth.storage.auth.AuthTable
import itmo.edugoolda.api.auth.storage.auth.DatabaseAuthStorage
import itmo.edugoolda.api.auth.storage.refresh_tokens.DatabaseRefreshTokensStorage
import itmo.edugoolda.api.auth.storage.refresh_tokens.RefreshTokensStorage
import itmo.edugoolda.api.auth.storage.refresh_tokens.RefreshTokensTable
import org.jetbrains.exposed.sql.Table
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {
    single { RefreshTokensTable } bind Table::class
    single { AuthTable } bind Table::class
    singleOf(::DatabaseAuthStorage) bind AuthStorage::class
    singleOf(::DatabaseRefreshTokensStorage) bind RefreshTokensStorage::class
    singleOf(::GetTokensUseCase)
}