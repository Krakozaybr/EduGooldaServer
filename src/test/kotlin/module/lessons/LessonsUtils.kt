package module.lessons

import itmo.edugoolda.api.lessons.domain.SolutionStatus
import itmo.edugoolda.api.lessons.storage.tables.GroupToLessonTable
import itmo.edugoolda.api.lessons.storage.tables.LessonTable
import itmo.edugoolda.api.lessons.storage.tables.MessagesTable
import itmo.edugoolda.api.lessons.storage.tables.SolutionsTable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import module.faker
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.time.Duration.Companion.days

object LessonsUtils {
    fun createLesson(
        author: String,
        groups: List<String>,
        name: String = faker.name.name(),
        description: String = "desc",
        isEstimatable: Boolean = true,
        deadline: Instant = Clock.System.now().plus(1.days),
        opensAt: Instant = Clock.System.now().minus(1.days),
    ) = transaction {
        val id = LessonTable.insertAndGetId {
            it[LessonTable.authorId] = UUID.fromString(author)
            it[LessonTable.name] = name
            it[LessonTable.description] = description
            it[LessonTable.deadline] = deadline
            it[LessonTable.opensAt] = opensAt
            it[LessonTable.isEstimatable] = isEstimatable
        }

        groups.forEach { groupId ->
            GroupToLessonTable.insert {
                it[GroupToLessonTable.lessonId] = id.value
                it[GroupToLessonTable.groupId] = UUID.fromString(groupId)
            }
        }

        id.value
    }

    fun checkLessonExists(lessonId: String) = transaction {
        LessonTable.selectAll().where { LessonTable.id eq UUID.fromString(lessonId) }.count() > 0
    }

    fun checkMessageExists(messageId: String) = transaction {
        MessagesTable.selectAll().where { MessagesTable.id eq UUID.fromString(messageId) }.count() > 0
    }

    fun createSolution(
        lessonId: String,
        author: String,
        teacherId: String,
        status: SolutionStatus = SolutionStatus.Pending
    ) = transaction {
        SolutionsTable.insertAndGetId {
            it[SolutionsTable.userId] = UUID.fromString(author)
            it[SolutionsTable.lessonId] = UUID.fromString(lessonId)
            it[SolutionsTable.teacherId] = UUID.fromString(teacherId)
            it[SolutionsTable.status] = status
        }
    }

    fun createMessage(
        solutionId: String,
        author: String,
        text: String
    ) = transaction {
        MessagesTable.insertAndGetId {
            it[MessagesTable.authorId] = UUID.fromString(author)
            it[MessagesTable.solutionId] = UUID.fromString(solutionId)
            it[MessagesTable.text] = text
        }.value
    }

    fun getSolution(
        lessonId: String,
        userId: String
    ) = transaction {
        SolutionsTable.select(SolutionsTable.id)
            .where {
                listOf(
                    SolutionsTable.userId eq UUID.fromString(userId),
                    SolutionsTable.lessonId eq UUID.fromString(lessonId)
                ).compoundAnd()
            }
            .singleOrNull()?.get(SolutionsTable.id)?.value
    }

    fun getSolutionStatus(
        solutionId: String
    ) = transaction {
        SolutionsTable.select(SolutionsTable.status)
            .where {
                listOf(
                    SolutionsTable.id eq UUID.fromString(solutionId),
                ).compoundAnd()
            }
            .singleOrNull()?.get(SolutionsTable.status)
    }

    fun getMessages(solutionId: String, userId: String) = transaction {
        MessagesTable.select(MessagesTable.id)
            .where {
                listOf(
                    MessagesTable.solutionId eq UUID.fromString(solutionId),
                    MessagesTable.authorId eq UUID.fromString(userId)
                ).compoundAnd()
            }
            .map { it[MessagesTable.id].value }
    }

    fun getMessages(solutionId: String) = transaction {
        MessagesTable.select(MessagesTable.id)
            .where {
                listOf(
                    MessagesTable.solutionId eq UUID.fromString(solutionId),
                ).compoundAnd()
            }
            .map { it[MessagesTable.id].value }
    }
}