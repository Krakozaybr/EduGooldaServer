package module.requests_and_invitations

import itmo.edugoolda.api.group.domain.model.JoinRequestStatus
import itmo.edugoolda.api.group.storage.entities.JoinRequestEntity
import itmo.edugoolda.api.group.storage.tables.BannedUsersTable
import itmo.edugoolda.api.group.storage.tables.JoinRequestTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object JoiningUtils {
    fun banStudent(
        groupId: String,
        studentId: String,
    ): Unit = transaction {
        BannedUsersTable.insert {
            it[BannedUsersTable.groupId] = UUID.fromString(groupId)
            it[BannedUsersTable.userId] = UUID.fromString(studentId)
        }
    }

    fun unbanStudent(
        groupId: String,
        studentId: String,
    ): Unit = transaction {
        BannedUsersTable.deleteWhere {
            listOf(
                BannedUsersTable.groupId eq UUID.fromString(groupId),
                BannedUsersTable.userId eq UUID.fromString(studentId)
            ).compoundAnd()
        }
    }

    fun addJoinRequestStudent(
        groupId: String,
        studentId: String,
    ) = transaction {
        JoinRequestTable.insertAndGetId {
            it[JoinRequestTable.groupId] = UUID.fromString(groupId)
            it[JoinRequestTable.userId] = UUID.fromString(studentId)
            it[JoinRequestTable.status] = JoinRequestStatus.Pending
        }.value
    }

    fun getJoinRequestStatus(
        id: String
    ) = transaction {
        JoinRequestEntity.findById(UUID.fromString(id))!!.status
    }

    fun isBanned(
        groupId: String,
        userId: String
    ) = transaction {
        BannedUsersTable.selectAll().where {
            listOf(
                BannedUsersTable.userId eq UUID.fromString(userId),
                BannedUsersTable.groupId eq UUID.fromString(groupId)
            ).compoundAnd()
        }.singleOrNull() != null
    }

    fun setStatusOfJoiningRequest(
        groupId: String,
        studentId: String,
        status: JoinRequestStatus
    ) = transaction {
        JoinRequestEntity.find {
            listOf(
                JoinRequestTable.groupId eq UUID.fromString(groupId),
                JoinRequestTable.userId eq UUID.fromString(studentId)
            ).compoundAnd()
        }.single().status = status
    }
}