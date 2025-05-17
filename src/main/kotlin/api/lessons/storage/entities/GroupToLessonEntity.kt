package itmo.edugoolda.api.lessons.storage.entities

import itmo.edugoolda.api.group.storage.entities.GroupEntity
import itmo.edugoolda.api.lessons.storage.tables.GroupToLessonTable
import itmo.edugoolda.utils.database.BaseEntity
import itmo.edugoolda.utils.database.BaseEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class GroupToLessonEntity(id: EntityID<UUID>) : BaseEntity(id, GroupToLessonTable) {
    companion object : BaseEntityClass<GroupToLessonEntity>(GroupToLessonTable)

    var groupId by GroupToLessonTable.groupId
    var lessonId by GroupToLessonTable.lessonId

    var group by GroupEntity referencedOn GroupToLessonTable.groupId
    var lesson by LessonEntity referencedOn GroupToLessonTable.lessonId
}