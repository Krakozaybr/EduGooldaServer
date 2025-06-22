package itmo.edugoolda.api.lessons.storage.lessons

import itmo.edugoolda.api.group.storage.entities.GroupEntity
import itmo.edugoolda.api.group.storage.entities.toGroupInfoDomain
import itmo.edugoolda.api.group.storage.tables.GroupTable
import itmo.edugoolda.api.group.storage.tables.GroupToUserTable
import itmo.edugoolda.api.lessons.domain.*
import itmo.edugoolda.api.lessons.exception.LessonNotFoundException
import itmo.edugoolda.api.lessons.storage.entities.*
import itmo.edugoolda.api.lessons.storage.tables.GroupToLessonTable
import itmo.edugoolda.api.lessons.storage.tables.LessonTable
import itmo.edugoolda.api.lessons.storage.tables.SolutionsTable
import itmo.edugoolda.api.user.storage.entities.toDomain
import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.Paged
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseLessonsStorage : LessonsStorage {
    override suspend fun createLesson(
        authorId: EntityIdentifier,
        name: String,
        description: String?,
        isEstimatable: Boolean,
        deadline: Instant?,
        opensAt: Instant?,
        groupIds: List<EntityIdentifier>
    ): LessonFullDetailsDomain {
        val lessonId = transaction {
            val lesson = LessonEntity.new {
                this.name = name
                this.description = description
                this.isEstimatable = isEstimatable
                this.deadline = deadline
                this.opensAt = opensAt
                this.authorId = EntityID(authorId.value, UserTable)
            }

            for (groupId in groupIds) {
                GroupToLessonEntity.new {
                    this.groupId = EntityID(groupId.value, GroupTable)
                    this.lesson = lesson
                }
            }

            EntityIdentifier.parse(lesson.id.value)
        }

        return getLessonTeacherDetails(lessonId)
    }

    override suspend fun checkIsLessonAuthor(
        lessonId: EntityIdentifier,
        userId: EntityIdentifier
    ): Boolean = transaction {
        LessonTable.select(LessonTable.id)
            .where {
                listOf(
                    LessonTable.id eq lessonId.value,
                    LessonTable.authorId eq userId.value
                ).compoundAnd()
            }
            .singleOrNull() != null
    }

    override suspend fun checkIsLessonStudent(
        lessonId: EntityIdentifier,
        userId: EntityIdentifier
    ): Boolean = transaction {
        UserTable
            .join(
                GroupToUserTable,
                JoinType.INNER,
                onColumn = UserTable.id,
                otherColumn = GroupToUserTable.userId
            )
            .join(
                GroupToLessonTable,
                JoinType.INNER,
                onColumn = GroupToUserTable.groupId,
                otherColumn = GroupToLessonTable.groupId
            )
            .join(
                LessonTable,
                JoinType.INNER,
                onColumn = GroupToLessonTable.lessonId,
                otherColumn = LessonTable.id,
                additionalConstraint = { LessonTable.id eq lessonId.value }
            )
            .select(UserTable.id)
            .singleOrNull() != null
    }

    override suspend fun deleteLesson(lessonId: EntityIdentifier): Unit = transaction {
        LessonEntity.findById(lessonId.value)?.delete()
    }

    override suspend fun getLessonsForTeacher(
        userId: EntityIdentifier,
        skip: Int,
        maxCount: Int,
        search: String?,
        groupId: EntityIdentifier?
    ): Paged<LessonInfoDomain> = transaction {
        Paged.of(
            skip = skip,
            count = maxCount,
            iterable = LessonTable
                .join(
                    UserTable,
                    JoinType.INNER,
                    onColumn = LessonTable.authorId,
                    otherColumn = UserTable.id,
                    additionalConstraint = { UserTable.id eq userId.value }
                )
                .run {
                    groupId ?: return@run this

                    join(
                        GroupToLessonTable,
                        JoinType.INNER,
                        onColumn = LessonTable.id,
                        otherColumn = GroupToLessonTable.lessonId,
                        additionalConstraint = { GroupToLessonTable.groupId eq groupId.value }
                    )
                }
                .selectAll()
                .run {
                    search ?: return@run this

                    orderBy(
                        Pair(
                            LessonTable.name like "%$search%",
                            SortOrder.DESC
                        )
                    )
                }
        ).map { LessonEntity.wrapRow(it).toLessonInfoDomain() }
    }

    override suspend fun getLessonsForStudent(
        userId: EntityIdentifier,
        skip: Int,
        maxCount: Int,
        search: String?,
        groupId: EntityIdentifier?
    ): Paged<LessonInfoDomain> = transaction {
        val now = Clock.System.now()

        Paged.of(
            skip = skip,
            count = maxCount,
            iterable = LessonTable
                .join(
                    GroupToLessonTable,
                    JoinType.INNER,
                    onColumn = LessonTable.id,
                    otherColumn = GroupToLessonTable.lessonId
                )
                .join(
                    GroupToUserTable,
                    JoinType.INNER,
                    onColumn = GroupToLessonTable.groupId,
                    otherColumn = GroupToUserTable.groupId,
                    additionalConstraint = { GroupToUserTable.userId eq userId.value }
                )
                .join(
                    UserTable,
                    JoinType.INNER,
                    onColumn = LessonTable.authorId,
                    otherColumn = UserTable.id
                )
                .select(UserTable.columns + LessonTable.columns)
                .where {
                    buildList {
                        add((LessonTable.opensAt eq null) or (LessonTable.opensAt lessEq now))

                        if (groupId != null) {
                            add(GroupToLessonTable.groupId eq groupId.value)
                        }
                    }.compoundAnd()
                }
                .run {
                    search ?: return@run this

                    orderBy(
                        Pair(
                            LessonTable.name like "%$search%",
                            SortOrder.DESC
                        )
                    )
                }
        ).map { LessonEntity.wrapRow(it).toLessonInfoDomain() }
    }

    override suspend fun getLessonStudentDetails(
        studentId: EntityIdentifier,
        lessonId: EntityIdentifier
    ): LessonStudentDetailsDomain = transaction {
        val lesson = LessonEntity.findById(lessonId.value)
            ?: throw LessonNotFoundException(lessonId)

        val solution = SolutionEntity.find {
            listOf(
                SolutionsTable.userId eq studentId.value,
                SolutionsTable.lessonId eq lessonId.value
            ).compoundAnd()
        }.singleOrNull()

        LessonStudentDetailsDomain(
            id = EntityIdentifier.parse(lesson.id.value),
            name = lesson.name,
            description = lesson.description,
            teacher = lesson.author.toDomain(),
            deadline = lesson.deadline,
            groups = lesson.groups.with(GroupEntity::subject).map { it.toGroupInfoDomain() },
            messages = solution?.messages
                ?.with(MessageEntity::author)
                ?.map { it.toSolutionMessageDomain() }
                ?: emptyList(),
            status = solution?.status ?: SolutionStatus.Pending,
            isEstimatable = lesson.isEstimatable,
        )
    }

    override suspend fun getLessonTeacherDetails(
        lessonId: EntityIdentifier
    ): LessonFullDetailsDomain = transaction {
        LessonEntity.findById(lessonId.value)?.toLessonFullDetailsDomain()
            ?: throw LessonNotFoundException(lessonId)
    }

    override suspend fun sendMessage(
        authorId: EntityIdentifier,
        solutionId: EntityIdentifier,
        message: String
    ): Unit = transaction {
        MessageEntity.new {
            this.authorId = EntityID(authorId.value, UserTable)
            this.solutionId = EntityID(solutionId.value, SolutionsTable)
            this.text = message
        }
    }

    override suspend fun getLessonEntity(
        lessonId: EntityIdentifier
    ): LessonEntityDomain? = transaction {
        LessonEntity.findById(lessonId.value)?.toLessonEntityDomain()
    }

    override suspend fun getSolutionEntity(
        lessonId: EntityIdentifier,
        studentId: EntityIdentifier
    ): SolutionEntityDomain? = transaction {
        SolutionEntity.find {
            listOf(
                SolutionsTable.userId eq studentId.value,
                SolutionsTable.lessonId eq lessonId.value
            ).compoundAnd()
        }.singleOrNull()?.toSolutionEntityDomain()
    }

    override suspend fun getSolutionEntity(
        solutionId: EntityIdentifier
    ): SolutionEntityDomain? = transaction {
        SolutionEntity.findById(solutionId.value)?.toSolutionEntityDomain()
    }

    override suspend fun getSolutionDetails(
        solutionId: EntityIdentifier
    ): SolutionDetailsDomain? = transaction {
        SolutionEntity.findById(solutionId.value)
            ?.toSolutionDetailsDomain()
    }

    override suspend fun createSolution(
        teacherId: EntityIdentifier,
        lessonId: EntityIdentifier,
        studentId: EntityIdentifier
    ): SolutionEntityDomain = transaction {
        SolutionEntity.new {
            this.lessonId = EntityID(lessonId.value, LessonTable)
            this.teacherId = EntityID(teacherId.value, UserTable)
            this.userId = EntityID(studentId.value, UserTable)
            this.status = SolutionStatus.Pending
        }.toSolutionEntityDomain()
    }

    override suspend fun getMessageEntity(
        id: EntityIdentifier
    ): MessageEntityDomain? = transaction {
        MessageEntity.findById(id.value)?.toMessageEntityDomain()
    }

    override suspend fun deleteMessage(id: EntityIdentifier): Unit = transaction {
        MessageEntity.findById(id.value)?.delete()
    }

    override suspend fun setSolutionStatus(
        solutionId: EntityIdentifier,
        status: SolutionStatus
    ): Unit = transaction {
        SolutionEntity.findById(solutionId.value)?.let { it.status = status }
    }

    override suspend fun getSolutionsForTeacher(
        teacherId: EntityIdentifier,
        skip: Int,
        maxCount: Int,
        lessonId: EntityIdentifier?,
        groupId: EntityIdentifier?,
        status: SolutionStatus?
    ): Paged<SolutionInfoDomain> = transaction {
        Paged.of(
            skip = skip,
            count = maxCount,
            iterable = SolutionsTable
                .run {
                    lessonId ?: return@run this

                    join(
                        LessonTable,
                        JoinType.INNER,
                        onColumn = SolutionsTable.lessonId,
                        otherColumn = LessonTable.id,
                        additionalConstraint = { LessonTable.authorId eq teacherId.value }
                    )
                }
                .run {
                    groupId ?: return@run this

                    join(
                        GroupToLessonTable,
                        JoinType.INNER,
                        onColumn = SolutionsTable.lessonId,
                        otherColumn = GroupToLessonTable.lessonId,
                        additionalConstraint = { GroupToLessonTable.groupId eq groupId.value }
                    )
                }
                .selectAll()
                .run {
                    status ?: return@run this

                    where {
                        SolutionsTable.status eq status
                    }
                }
        ).map { SolutionEntity.wrapRow(it).toSolutionInfoDomain() }
    }
}