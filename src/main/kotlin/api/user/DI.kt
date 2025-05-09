package itmo.edugoolda.api.user

import itmo.edugoolda.api.user.storage.DatabaseUserStorage
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.api.user.storage.tables.UserTable
import org.jetbrains.exposed.sql.Table
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val userModule = module {
    single { UserTable } bind Table::class
    singleOf(::DatabaseUserStorage) bind UserStorage::class
}