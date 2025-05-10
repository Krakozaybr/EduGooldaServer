package module.group

import itmo.edugoolda.api.group.storage.entities.UserFavouriteGroupEntity
import itmo.edugoolda.api.group.storage.tables.GroupTable
import itmo.edugoolda.api.group.storage.tables.GroupToUserTable
import itmo.edugoolda.api.group.storage.tables.UserFavouriteGroupTable
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.insert
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

    fun setFavourite(
        userId: String,
        groupId: String,
        isFavourite: Boolean
    ): Unit = transaction {
        UserFavouriteGroupTable.insert {
            it[UserFavouriteGroupTable.isFavourite] = isFavourite
            it[UserFavouriteGroupTable.groupId] = UUID.fromString(groupId)
            it[UserFavouriteGroupTable.userId] = UUID.fromString(userId)
        }
    }

    fun getFavourite(
        userId: String,
        groupId: String
    ): Boolean = transaction {
        UserFavouriteGroupEntity.find {
            listOf(
                UserFavouriteGroupTable.groupId eq UUID.fromString(groupId),
                UserFavouriteGroupTable.userId eq UUID.fromString(userId),
            ).compoundAnd()
        }.singleOrNull()?.isFavourite == true
    }
}