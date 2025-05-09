package itmo.edugoolda.api.group.storage.group_request

import itmo.edugoolda.api.group.domain.model.JoinRequestDomain
import itmo.edugoolda.api.group.domain.model.JoinRequestInfoDomain
import itmo.edugoolda.api.group.domain.model.JoinRequestStatus
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.JoinRequestNotFoundException
import itmo.edugoolda.api.group.storage.entities.*
import itmo.edugoolda.api.group.storage.tables.GroupCodeTable
import itmo.edugoolda.api.group.storage.tables.GroupTable
import itmo.edugoolda.api.group.storage.tables.GroupToUserTable
import itmo.edugoolda.api.group.storage.tables.JoinRequestTable
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.Paged
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.Duration.Companion.days

class DatabaseGroupRequestStorage : GroupRequestStorage {

    companion object {
        private val CODE_LIVE_TIME = 1.days
    }

    override suspend fun getGroupIdByCode(code: String): EntityIdentifier? = transaction {
        GroupCodeEntity.find {
            GroupCodeTable.id eq code
        }.singleOrNull()?.groupId?.value?.let(EntityIdentifier::parse)
    }

    override suspend fun checkPendingJoinRequestExists(
        userId: EntityIdentifier,
        groupId: EntityIdentifier
    ): Boolean = transaction {
        JoinRequestEntity.find {
            listOf(
                JoinRequestTable.groupId eq groupId.value,
                JoinRequestTable.status eq JoinRequestStatus.Pending,
                JoinRequestTable.userId eq userId.value
            ).compoundAnd()
        }.empty().not()
    }

    override suspend fun getJoinRequestInfoById(
        identifier: EntityIdentifier
    ): JoinRequestInfoDomain? = transaction {
        JoinRequestEntity.findById(identifier.value)?.toJoinRequestInfoDomain()
    }

    override suspend fun getGroupJoinRequests(
        groupId: EntityIdentifier,
        skip: Int,
        limit: Int
    ): Paged<JoinRequestDomain> = transaction {
        val group = GroupEntity.findById(groupId.value)
            ?: throw GroupNotFoundException(groupId)

        Paged.of(
            skip = skip,
            count = limit,
            iterable = group.requests
                .with(JoinRequestEntity::user),
        ).map { it.toDomain() }
    }

    override suspend fun getTeacherJoinRequests(
        userId: EntityIdentifier,
        skip: Int,
        limit: Int
    ): Paged<JoinRequestDomain> = transaction {
        Paged.of(
            skip = skip,
            count = limit,
            iterable = UserTable
                .join(
                    GroupTable,
                    JoinType.INNER,
                    onColumn = UserTable.id,
                    otherColumn = GroupTable.ownerId
                )
                .join(
                    JoinRequestTable,
                    JoinType.INNER,
                    onColumn = GroupTable.id,
                    otherColumn = JoinRequestTable.groupId
                )
                .select(JoinRequestTable.columns + UserTable.columns)
        ).map { JoinRequestEntity.wrapRow(it).toDomain() }
    }

    override suspend fun sendAddToGroupRequest(
        groupId: EntityIdentifier,
        userId: EntityIdentifier
    ): Unit = transaction {

        val group = GroupEntity.findById(groupId.value)
            ?: throw GroupNotFoundException(groupId)

        val user = UserEntity.findById(userId.value)
            ?: throw UserNotFoundException(userId)

        JoinRequestEntity.new {
            this.user = user
            this.group = group
            this.status = JoinRequestStatus.Pending
        }
    }

    override suspend fun cancelAddToGroupRequest(requestId: EntityIdentifier) = transaction {
        val request = JoinRequestEntity.findById(requestId.value)
            ?: throw JoinRequestNotFoundException(requestId)

        request.status = JoinRequestStatus.Cancelled
    }

    override suspend fun acceptAddToGroupRequest(requestId: EntityIdentifier): Unit = transaction {
        val request = JoinRequestEntity.findById(requestId.value)
            ?: throw JoinRequestNotFoundException(requestId)

        GroupToUserTable.insert {
            it[groupId] = request.groupId
            it[userId] = request.userId
        }

        request.status = JoinRequestStatus.Accepted
    }

    override suspend fun declineAddToGroupRequest(requestId: EntityIdentifier) = transaction {
        val request = JoinRequestEntity.findById(requestId.value)
            ?: throw JoinRequestNotFoundException(requestId)

        request.status = JoinRequestStatus.Declined
    }

    @OptIn(ExperimentalStdlibApi::class)
    override suspend fun generateCodeForGroup(
        groupId: EntityIdentifier
    ): String = transaction {
        val group = GroupEntity.findById(groupId.value)
            ?: throw GroupNotFoundException(groupId)

        val existingCode = GroupCodeEntity.find {
            GroupCodeTable.groupId eq groupId.value
        }.singleOrNull()

        if (existingCode != null) {
            val expirationDate = existingCode.createdAt
                .toInstant(TimeZone.currentSystemDefault())
                .plus(CODE_LIVE_TIME)

            if (expirationDate > Clock.System.now()) {
                return@transaction existingCode.code.value
            }

            existingCode.delete()
        }

        val format = HexFormat {
            upperCase = true
            number {
                minLength = 6
            }
        }

        val minValue = "000000".hexToInt(format)
        val maxValue = "FFFFFF".hexToInt(format)

        val allCodes = GroupCodeEntity
            .all()
            .map { it.code.value }
            .toSet()

        val result: String

        while (true) {
            val code = (minValue..maxValue).random().toHexString(format)

            if (code !in allCodes) {
                result = code
                break
            }
        }

        GroupCodeEntity.new(result) {
            this.group = group
        }

        result
    }
}