package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.lessons.dto.toDto
import itmo.edugoolda.api.lessons.exception.MustBeLessonAuthorException
import itmo.edugoolda.api.lessons.exception.SolutionNotFoundException
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val SOLUTION_PATH_KEY = "solution_id"

fun Route.solutionDetailsRoute(koin: Koin) {
    val lessonsStorage = koin.get<LessonsStorage>()

    authenticate {
        get("/solution/{$SOLUTION_PATH_KEY}") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val solutionId = idParameter(SOLUTION_PATH_KEY)

            val solution = lessonsStorage.getSolutionDetails(solutionId)
                ?: throw SolutionNotFoundException(solutionId)

            if (solution.lesson.teacher.id != userId) {
                throw MustBeLessonAuthorException()
            }

            call.respond(
                HttpStatusCode.OK,
                solution.toDto()
            )
        }
    }
}