package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.lessons.dto.SolutionInfoDTO
import itmo.edugoolda.api.lessons.dto.SolutionsListParams
import itmo.edugoolda.api.lessons.dto.toDto
import itmo.edugoolda.api.lessons.exception.LessonNotFoundException
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.solutionListRoute(koin: Koin) {
    val lessonsStorage = koin.get<LessonsStorage>()
    val userStorage = koin.get<UserStorage>()
    val groupStorage = koin.get<GroupStorage>()

    authenticate {
        get("/solutions") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val params = SolutionsListParams.from(call.queryParameters)

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            if (user.role != UserRole.Teacher) {
                throw UnsuitableUserRoleException(UserRole.Teacher)
            }

            if (params.groupId != null && groupStorage.getGroupEntity(params.groupId) == null) {
                throw GroupNotFoundException(params.groupId)
            }

            if (params.lessonId != null && lessonsStorage.getLessonEntity(params.lessonId) == null) {
                throw LessonNotFoundException(params.lessonId)
            }

            val result = lessonsStorage.getSolutionsForTeacher(
                teacherId = userId,
                skip = params.paginationDto.skip,
                maxCount = params.paginationDto.pageSize,
                groupId = params.groupId,
                lessonId = params.lessonId,
                status = params.status,
            )

            call.respond(
                HttpStatusCode.OK,
                SolutionListResponse(
                    items = result.entities.map { it.toDto() },
                    total = result.total
                )
            )
        }
    }
}

@Serializable
data class SolutionListResponse(
    @SerialName("solutions") val items: List<SolutionInfoDTO>,
    @SerialName("total") val total: Int
)