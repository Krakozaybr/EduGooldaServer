package itmo.edugoolda.utils.database

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

abstract class BaseTable(
    name: String = "",
    columnName: String = "id"
) : UUIDTable(name, columnName) {
    val createdAt: Column<LocalDateTime> = datetime("created")
        .defaultExpression(CurrentDateTime)

    val modified: Column<LocalDateTime?> = datetime("updated").nullable()
}