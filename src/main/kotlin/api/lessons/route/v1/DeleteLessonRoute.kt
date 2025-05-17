package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.lessons.exception.LessonNotFoundException
import itmo.edugoolda.api.lessons.exception.MustBeLessonAuthorException
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val LESSON_PATH_KEY = "lesson_id"

fun Route.deleteLessonRoute(koin: Koin) {
    val lessonsStorage = koin.get<LessonsStorage>()

    authenticate {
        delete("/lesson/{$LESSON_PATH_KEY}") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val lessonId = idParameter(LESSON_PATH_KEY)

            if (lessonsStorage.getLessonEntity(lessonId) == null) {
                throw LessonNotFoundException(lessonId)
            }

            if (
                !lessonsStorage.checkIsLessonAuthor(
                    lessonId = lessonId,
                    userId = userId
                )
            ) {
                throw MustBeLessonAuthorException()
            }

            lessonsStorage.deleteLesson(lessonId)

            call.respond(HttpStatusCode.OK)
        }
    }
}