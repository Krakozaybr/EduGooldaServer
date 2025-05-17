package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.lessons.dto.LessonInfoDTO
import itmo.edugoolda.api.lessons.dto.LessonsListParams
import itmo.edugoolda.api.lessons.dto.toDto
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.lessonListRoute(koin: Koin) {
    val lessonsStorage = koin.get<LessonsStorage>()
    val userStorage = koin.get<UserStorage>()

    authenticate {
        get("/lessons") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            val params = LessonsListParams.from(call.queryParameters)

            val res = when (user.role) {
                UserRole.Teacher -> lessonsStorage::getLessonsForTeacher
                UserRole.Student -> lessonsStorage::getLessonsForStudent
            }.invoke(
                userId,
                params.paginationDto.skip,
                params.paginationDto.pageSize,
                params.query,
                params.groupId
            )

            call.respond(
                HttpStatusCode.OK,
                LessonsListResponse(
                    items = res.entities.map { it.toDto() },
                    total = res.total
                )
            )
        }
    }
}

@Serializable
data class LessonsListResponse(
    @SerialName("lessons") val items: List<LessonInfoDTO>,
    @SerialName("total") val total: Int
)