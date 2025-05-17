package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.exception.MustBeParticipantException
import itmo.edugoolda.api.group.utils.maxLengthNullable
import itmo.edugoolda.api.lessons.domain.SolutionStatus
import itmo.edugoolda.api.lessons.exception.DeadlineHasPassedException
import itmo.edugoolda.api.lessons.exception.LessonNotFoundException
import itmo.edugoolda.api.lessons.exception.SolutionMessageException
import itmo.edugoolda.api.lessons.exception.SolutionWasReviewedException
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.api.lessons.storage.tables.MessagesTable
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

private const val LESSON_PATH_KEY = "lesson_id"

fun Route.studentSendMessageRoute(koin: Koin) {
    val lessonsStorage = koin.get<LessonsStorage>()

    authenticate {
        post<SendMessageRequest>("/lesson/{$LESSON_PATH_KEY}/message") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val lessonId = idParameter(LESSON_PATH_KEY)

            val lessonEntity = lessonsStorage.getLessonEntity(lessonId)
                ?: throw LessonNotFoundException(lessonId)

            if (lessonEntity.opensAt != null && lessonEntity.opensAt > Clock.System.now()) {
                throw LessonNotFoundException(lessonId)
            }

            if (
                !lessonsStorage.checkIsLessonStudent(
                    lessonId = lessonId,
                    userId = userId
                ) || lessonEntity.authorId == userId
            ) {
                throw MustBeParticipantException()
            }

            if (lessonEntity.deadline != null && lessonEntity.deadline < Clock.System.now()) {
                throw DeadlineHasPassedException()
            }

            if (it.message.length !in 1..MessagesTable.text.maxLengthNullable) {
                throw SolutionMessageException()
            }

            val solutionEntity = lessonsStorage.getSolutionEntity(
                lessonId = lessonId,
                studentId = userId
            ) ?: lessonsStorage.createSolution(
                teacherId = lessonEntity.authorId,
                studentId = userId,
                lessonId = lessonId
            )

            if (solutionEntity.status == SolutionStatus.Reviewed) {
                throw SolutionWasReviewedException()
            }

            lessonsStorage.sendMessage(
                authorId = userId,
                message = it.message,
                solutionId = solutionEntity.id
            )

            call.respond(HttpStatusCode.OK)
        }
    }
}

@Serializable
data class SendMessageRequest(
    @SerialName("message") val message: String
)