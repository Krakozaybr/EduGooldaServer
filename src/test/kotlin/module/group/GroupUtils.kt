package module.group

import itmo.edugoolda.api.group.storage.tables.GroupTable
import itmo.edugoolda.api.group.storage.tables.GroupToUserTable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object GroupUtils {

    val DEFAULT_GROUP_NAME = "Test group"

    fun createGroupInDatabase(
        name: String = DEFAULT_GROUP_NAME,
        description: String? = null,
        ownerId: String,
        subjectId: String,
        isActive: Boolean = true
    ) = transaction {
        GroupTable.insertAndGetId {
            it[GroupTable.name] = name
            it[GroupTable.description] = description
            it[GroupTable.ownerId] = UUID.fromString(ownerId)
            it[GroupTable.subjectId] = UUID.fromString(subjectId)
            it[GroupTable.isActive] = isActive
        }.value
    }

    fun addStudentToGroup(
        studentId: String,
        groupId: String
    ) = transaction {
        GroupToUserTable.insertAndGetId {
            it[GroupToUserTable.groupId] = UUID.fromString(groupId)
            it[GroupToUserTable.userId] = UUID.fromString(studentId)
        }.value
    }
}