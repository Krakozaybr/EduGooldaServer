package itmo.edugoolda.api.lessons

import itmo.edugoolda.api.lessons.storage.lessons.DatabaseLessonsStorage
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.api.lessons.storage.tables.GroupToLessonTable
import itmo.edugoolda.api.lessons.storage.tables.LessonTable
import itmo.edugoolda.api.lessons.storage.tables.MessagesTable
import itmo.edugoolda.api.lessons.storage.tables.SolutionsTable
import org.jetbrains.exposed.sql.Table
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val lessonsModule = module {
    factory { GroupToLessonTable } bind Table::class
    factory { LessonTable } bind Table::class
    factory { MessagesTable } bind Table::class
    factory { SolutionsTable } bind Table::class
    singleOf(::DatabaseLessonsStorage) bind LessonsStorage::class
}