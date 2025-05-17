package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.error.exceptions.PrintableEnum
import itmo.edugoolda.api.lessons.domain.SolutionStatus
import itmo.edugoolda.api.lessons.exception.MustBeLessonAuthorException
import itmo.edugoolda.api.lessons.exception.SolutionNotFoundException
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

private const val SOLUTION_ID_PATH_PARAMETER = "solution_id"

fun Route.setSolutionStatusRoute(koin: Koin) {
    val lessonsStorage = koin.get<LessonsStorage>()

    authenticate {
        put<SetSolutionStatusRequest>("/solution/{$SOLUTION_ID_PATH_PARAMETER}/status") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val solutionId = idParameter(SOLUTION_ID_PATH_PARAMETER)

            val solution = lessonsStorage.getSolutionEntity(solutionId)
                ?: throw SolutionNotFoundException(solutionId)

            val status = PrintableEnum.parseOrThrow<SolutionStatus>(it.status)

            if (solution.teacherId != userId) {
                throw MustBeLessonAuthorException()
            }

            lessonsStorage.setSolutionStatus(solutionId, status)

            call.respond(HttpStatusCode.OK)
        }
    }
}

@Serializable
data class SetSolutionStatusRequest(
    @SerialName("status") val status: String
)