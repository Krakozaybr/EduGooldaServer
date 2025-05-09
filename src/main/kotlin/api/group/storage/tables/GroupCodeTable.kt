package itmo.edugoolda.api.group.storage.tables

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.CurrentDateTime
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object GroupCodeTable : IdTable<String>("group_codes") {
    override val id: Column<EntityID<String>> = varchar("code", 6).entityId()
    val groupId = reference(
        name = "group_id",
        refColumn = GroupTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    val createdAt: Column<LocalDateTime> = datetime("created")
        .defaultExpression(CurrentDateTime)

    override val primaryKey = PrimaryKey(id)
}