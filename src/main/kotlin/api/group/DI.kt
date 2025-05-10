package itmo.edugoolda.api.group

import itmo.edugoolda.api.group.domain.use_case.SendJoinRequestUseCase
import itmo.edugoolda.api.group.storage.ban.DatabaseGroupBanStorage
import itmo.edugoolda.api.group.storage.ban.GroupBanStorage
import itmo.edugoolda.api.group.storage.group.DatabaseGroupStorage
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.storage.group_request.DatabaseGroupRequestStorage
import itmo.edugoolda.api.group.storage.group_request.GroupRequestStorage
import itmo.edugoolda.api.group.storage.subject.DatabaseSubjectStorage
import itmo.edugoolda.api.group.storage.subject.SubjectStorage
import itmo.edugoolda.api.group.storage.tables.*
import org.jetbrains.exposed.sql.Table
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val groupModule = module {
    singleOf(::DatabaseSubjectStorage) bind SubjectStorage::class
    singleOf(::DatabaseGroupStorage) bind GroupStorage::class
    singleOf(::DatabaseGroupBanStorage) bind GroupBanStorage::class
    singleOf(::DatabaseGroupRequestStorage) bind GroupRequestStorage::class
    singleOf(::SendJoinRequestUseCase)
    single { BannedUsersTable } bind Table::class
    single { GroupCodeTable } bind Table::class
    single { GroupTable } bind Table::class
    single { GroupToUserTable } bind Table::class
    single { JoinRequestTable } bind Table::class
    single { SubjectTable } bind Table::class
    single { UserFavouriteGroupTable } bind Table::class
}
