package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.exception.MustBeParticipantException
import itmo.edugoolda.api.lessons.dto.toDto
import itmo.edugoolda.api.lessons.exception.LessonNotFoundException
import itmo.edugoolda.api.lessons.exception.MustBeLessonAuthorException
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import kotlinx.datetime.Clock
import org.koin.core.Koin

private const val LESSON_PATH_KEY = "lesson_id"

fun Route.lessonDetailsRoute(koin: Koin) {
    val lessonsStorage = koin.get<LessonsStorage>()
    val userStorage = koin.get<UserStorage>()

    authenticate {
        get("/lesson/{${LESSON_PATH_KEY}}") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            val lessonId = idParameter(LESSON_PATH_KEY)

            val lessonEntity = lessonsStorage.getLessonEntity(lessonId)
                ?: throw LessonNotFoundException(lessonId)

            when (user.role) {
                UserRole.Teacher -> {
                    if (!lessonsStorage.checkIsLessonAuthor(lessonId = lessonId, userId = userId)) {
                        throw MustBeLessonAuthorException()
                    }

                    val details = lessonsStorage.getLessonTeacherDetails(lessonId)

                    call.respond(
                        HttpStatusCode.OK,
                        details.toDto()
                    )
                }

                UserRole.Student -> {
                    if (!lessonsStorage.checkIsLessonStudent(lessonId = lessonId, userId = userId)) {
                        throw MustBeParticipantException()
                    }

                    if (lessonEntity.opensAt != null && lessonEntity.opensAt > Clock.System.now()) {
                        throw LessonNotFoundException(lessonId)
                    }

                    val details = lessonsStorage.getLessonStudentDetails(
                        studentId = userId,
                        lessonId = lessonId
                    )

                    call.respond(
                        HttpStatusCode.OK,
                        details.toDto()
                    )
                }
            }
        }
    }
}
