package itmo.edugoolda.utils.database

import itmo.edugoolda.utils.toCurrentLocalDateTime
import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.toEntity
import org.jetbrains.exposed.sql.Column

abstract class BaseEntityClass<out E : BaseEntity>(
    table: BaseTable
) : UUIDEntityClass<E>(table) {
    init {
        EntityHook.subscribe { change ->
            val changedEntity = change.toEntity(this)

            if (change.changeType != EntityChangeType.Updated) return@subscribe

            val now = nowUTC()
            changedEntity?.let {
                if (it.writeValues[table.modified as Column<*>] == null) {
                    it.modifiedAt = now
                }
            }
        }
    }

    private fun nowUTC() = Clock.System.now().toCurrentLocalDateTime()
}