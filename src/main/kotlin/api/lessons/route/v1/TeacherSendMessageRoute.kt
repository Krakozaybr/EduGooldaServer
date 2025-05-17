package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.utils.maxLengthNullable
import itmo.edugoolda.api.lessons.exception.MustBeLessonAuthorException
import itmo.edugoolda.api.lessons.exception.SolutionMessageException
import itmo.edugoolda.api.lessons.exception.SolutionNotFoundException
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.api.lessons.storage.tables.MessagesTable
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val SOLUTION_PATH_KEY = "solution_id"

fun Route.teacherSendMessageRoute(koin: Koin) {
    val lessonsStorage = koin.get<LessonsStorage>()

    authenticate {
        post<SendMessageRequest>("/solution/{${SOLUTION_PATH_KEY}}/message") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val solutionId = idParameter(SOLUTION_PATH_KEY)

            val solution = lessonsStorage.getSolutionEntity(solutionId)
                ?: throw SolutionNotFoundException(solutionId)

            if (solution.teacherId != userId) {
                throw MustBeLessonAuthorException()
            }

            if (it.message.length !in 1..MessagesTable.text.maxLengthNullable) {
                throw SolutionMessageException()
            }

            lessonsStorage.sendMessage(
                authorId = userId,
                solutionId = solutionId,
                message = it.message
            )

            call.respond(HttpStatusCode.OK)
        }
    }
}