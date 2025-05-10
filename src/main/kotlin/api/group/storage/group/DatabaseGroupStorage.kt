package itmo.edugoolda.api.group.storage.group

import itmo.edugoolda.api.group.domain.model.GroupDetailsDomain
import itmo.edugoolda.api.group.domain.model.GroupEntityDomain
import itmo.edugoolda.api.group.domain.model.GroupInfoDomain
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.SubjectNotFoundException
import itmo.edugoolda.api.group.storage.entities.*
import itmo.edugoolda.api.group.storage.tables.*
import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.entities.UserEntity
import itmo.edugoolda.api.user.storage.entities.toDomain
import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.Paged
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseGroupStorage : GroupStorage {
    override suspend fun createGroup(
        name: String,
        description: String?,
        subjectId: EntityIdentifier,
        ownerId: EntityIdentifier
    ): EntityIdentifier = transaction {

        val subject = SubjectEntity.findById(subjectId.value)
            ?: throw SubjectNotFoundException(subjectId)

        val owner = UserEntity.findById(ownerId.value)
            ?: throw UserNotFoundException(ownerId)

        GroupEntity.new {
            this.name = name
            this.description = description
            this.owner = owner
            this.subject = subject
        }.id.value.let(EntityIdentifier::parse)
    }

    override suspend fun updateGroup(
        groupId: EntityIdentifier,
        name: String,
        description: String?,
        subjectId: EntityIdentifier,
        isActive: Boolean
    ) = transaction {

        val subject = SubjectEntity.findById(subjectId.value)
            ?: throw SubjectNotFoundException(subjectId)

        val group = GroupEntity.findById(groupId.value)
            ?: throw GroupNotFoundException(groupId)

        group.name = name
        group.description = description
        group.subject = subject
        group.isActive = isActive
    }

    override suspend fun deleteGroup(groupId: EntityIdentifier): Unit = transaction {
        GroupTable.deleteWhere { GroupTable.id eq groupId.value }
    }

    override suspend fun getGroupEntity(groupId: EntityIdentifier): GroupEntityDomain? = transaction {
        GroupEntity.findById(groupId.value)?.toGroupEntityDomain()
    }

    override suspend fun removeUsersFromGroup(
        id: EntityIdentifier,
        groupId: EntityIdentifier
    ): Boolean = transaction {
        GroupToUserTable
            .deleteWhere {
                (userId eq id.value) and (GroupToUserTable.groupId eq groupId.value)
            } == 1
    }

    override suspend fun getGroupStudents(
        skip: Int,
        maxCount: Int,
        groupId: EntityIdentifier
    ): Paged<UserInfoDomain> = transaction {
        val group = GroupEntity.findById(groupId.value)
            ?: throw GroupNotFoundException(groupId)

        Paged.of(
            skip = skip,
            count = maxCount,
            iterable = group.students
        ).map { it.toDomain() }
    }

    override suspend fun getGroupStudentsCount(groupId: EntityIdentifier): Int = transaction {
        val group = GroupEntity.findById(groupId.value)
            ?: throw GroupNotFoundException(groupId)

        group.students.count().toInt()
    }

    override suspend fun getStudentGroups(
        skip: Int,
        maxCount: Int,
        userId: EntityIdentifier,
        query: String?,
        subjectName: String?,
        isFavourite: Boolean?
    ): Paged<GroupInfoDomain> = transaction {
        Paged.of(
            skip = skip,
            count = maxCount,
            iterable = GroupTable
                .join(
                    otherTable = GroupToUserTable,
                    joinType = JoinType.INNER,
                    onColumn = GroupTable.id,
                    otherColumn = GroupToUserTable.groupId
                )
                .join(
                    otherTable = UserTable,
                    joinType = JoinType.INNER,
                    onColumn = GroupToUserTable.userId,
                    otherColumn = UserTable.id,
                    additionalConstraint = { UserTable.id eq userId.value }
                )
                .join(
                    otherTable = SubjectTable,
                    joinType = JoinType.INNER,
                    onColumn = GroupTable.subjectId,
                    otherColumn = SubjectTable.id
                )
                .join(
                    otherTable = UserFavouriteGroupTable,
                    joinType = JoinType.LEFT,
                    onColumn = UserTable.id,
                    otherColumn = UserFavouriteGroupTable.userId,
                    additionalConstraint = {
                        UserFavouriteGroupTable.groupId eq GroupTable.id
                    }
                )
                .selectAll()
                .run {
                    isFavourite ?: return@run this

                    where {
                        when (isFavourite) {
                            true -> UserFavouriteGroupTable.isFavourite eq true
                            false -> listOf(
                                UserFavouriteGroupTable.isFavourite eq false,
                                UserFavouriteGroupTable.id eq null
                            ).compoundOr()
                        }
                    }
                }
                .run {
                    orderBy(
                        *buildList {
                            if (query != null) {
                                add(GroupTable.name like query to SortOrder.DESC)
                            }

                            if (subjectName != null) {
                                add(SubjectTable.name like subjectName to SortOrder.DESC)
                            }
                        }.toTypedArray()
                    )
                }
        ).map {
            GroupEntity.wrapRow(it).toGroupInfoDomain(
                it.getOrNull(UserFavouriteGroupTable.isFavourite) ?: false
            )
        }
    }

    override suspend fun getTeacherGroups(
        skip: Int,
        maxCount: Int,
        userId: EntityIdentifier,
        query: String?,
        subjectName: String?,
        isFavourite: Boolean?
    ): Paged<GroupInfoDomain> = transaction {
        Paged.of(
            skip = skip,
            count = maxCount,
            iterable = GroupTable
                .join(
                    otherTable = UserTable,
                    joinType = JoinType.INNER,
                    onColumn = GroupTable.ownerId,
                    otherColumn = UserTable.id,
                    additionalConstraint = { UserTable.id eq userId.value }
                )
                .join(
                    otherTable = SubjectTable,
                    joinType = JoinType.INNER,
                    onColumn = GroupTable.subjectId,
                    otherColumn = SubjectTable.id
                )
                .join(
                    otherTable = UserFavouriteGroupTable,
                    joinType = JoinType.LEFT,
                    onColumn = GroupTable.id,
                    otherColumn = UserFavouriteGroupTable.groupId,
                    additionalConstraint = {
                        UserFavouriteGroupTable.userId eq UserTable.id
                    }
                )
                .selectAll()
                .run {
                    isFavourite ?: return@run this

                    where {
                        when (isFavourite) {
                            true -> UserFavouriteGroupTable.isFavourite eq true
                            false -> listOf(
                                UserFavouriteGroupTable.isFavourite eq false,
                                UserFavouriteGroupTable.id eq null
                            ).compoundOr()
                        }
                    }
                }
                .run {
                    orderBy(
                        *buildList {
                            if (query != null) {
                                add(GroupTable.name like query to SortOrder.DESC)
                            }

                            if (subjectName != null) {
                                add(SubjectTable.name like subjectName to SortOrder.DESC)
                            }
                        }.toTypedArray()
                    )
                }
        ).map {
            GroupEntity.wrapRow(it).toGroupInfoDomain(
                it.getOrNull(UserFavouriteGroupTable.isFavourite) ?: false
            )
        }
    }

    override suspend fun getGroupDetails(
        groupId: EntityIdentifier,
        userId: EntityIdentifier
    ): GroupDetailsDomain = transaction {

        val group = GroupEntity.findById(groupId.value)
            ?: throw GroupNotFoundException(groupId)

        val isFavourite = UserFavouriteGroupEntity.find {
            listOf(
                UserFavouriteGroupTable.groupId eq groupId.value,
                UserFavouriteGroupTable.userId eq userId.value
            ).compoundAnd()
        }.singleOrNull()?.isFavourite ?: false

        GroupDetailsDomain(
            id = EntityIdentifier.parse(group.id.value),
            name = group.name,
            description = group.description,
            subjectDomain = group.subject.toDomain(),
            owner = group.owner.toDomain(),
            studentsCount = group.students.count().toInt(),
            requestsCount = group.requests.count().toInt(),
            bannedCount = group.banned.count().toInt(),
            newSolutionsCount = 0, // TODO: implement when solutions and tasks are ready
            tasksCount = 0, // TODO: implement when solutions and tasks are ready
            isActive = group.isActive,
            createdAt = group.createdAt,
            isFavourite = isFavourite
        )
    }

    override suspend fun getGroupBanned(
        skip: Int,
        maxCount: Int,
        groupId: EntityIdentifier
    ): Paged<UserInfoDomain> = transaction {
        val group = GroupEntity.findById(groupId.value)
            ?: throw GroupNotFoundException(groupId)

        Paged.of(
            skip = skip,
            count = maxCount,
            iterable = group
                .banned
                .with(BannedEntity::user)
        ).map { it.user.toDomain() }
    }

    override suspend fun checkStudentIsParticipant(
        groupId: EntityIdentifier,
        userId: EntityIdentifier
    ): Boolean = transaction {
        val relation = GroupToUserTable.select(GroupToUserTable.id)
            .where { (GroupToUserTable.groupId eq groupId.value) and (GroupToUserTable.userId eq userId.value) }
            .singleOrNull()

        relation != null
    }

    override suspend fun checkUserIsBanned(
        groupId: EntityIdentifier,
        userId: EntityIdentifier
    ): Boolean = transaction {
        BannedEntity.find {
            (BannedUsersTable.groupId eq groupId.value) and (BannedUsersTable.userId eq userId.value)
        }.singleOrNull() != null
    }

    override suspend fun setIsFavourite(
        userId: EntityIdentifier,
        groupId: EntityIdentifier,
        isFavourite: Boolean
    ): Unit = transaction {
        val entity = UserFavouriteGroupEntity.find {
            listOf(
                UserFavouriteGroupTable.groupId eq groupId.value,
                UserFavouriteGroupTable.userId eq userId.value
            ).compoundAnd()
        }.singleOrNull()

        if (entity == null) {
            UserFavouriteGroupEntity.new {
                this.groupId = EntityID(groupId.value, GroupTable)
                this.userId = EntityID(userId.value, UserTable)
                this.isFavourite = isFavourite
            }
        } else {
            entity.isFavourite = isFavourite
        }
    }
}