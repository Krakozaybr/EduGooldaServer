package itmo.edugoolda.utils.database

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

abstract class BaseEntity(id: EntityID<UUID>, table: BaseTable) : UUIDEntity(id) {
    val createdAt: LocalDateTime by table.createdAt
    var modifiedAt: LocalDateTime? by table.modified
}