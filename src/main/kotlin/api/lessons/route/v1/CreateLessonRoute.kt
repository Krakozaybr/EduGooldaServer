package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.error.exceptions.IdFormatException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.utils.maxLength
import itmo.edugoolda.api.group.utils.maxLengthNullable
import itmo.edugoolda.api.lessons.dto.toDto
import itmo.edugoolda.api.lessons.exception.*
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.api.lessons.storage.tables.LessonTable
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.createLessonRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()
    val lessonStorage = koin.get<LessonsStorage>()
    val userStorage = koin.get<UserStorage>()

    authenticate {
        post<CreateLessonRequest>("/lesson") { request ->
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            if (user.role != UserRole.Teacher) {
                throw UnsuitableUserRoleException(UserRole.Teacher)
            }

            if (
                request.name.isBlank()
                || request.name.length !in 1..LessonTable.name.maxLengthNullable
            ) {
                throw LessonNameException()
            }

            if (
                request.description != null
                && request.description.length !in 1..LessonTable.description.maxLength
            ) {
                throw LessonDescriptionException()
            }

            val groupIds = request.groupIds.map {
                EntityIdentifier.parse(it) ?: throw IdFormatException()
            }

            if (request.groupIds.isEmpty()) {
                throw GroupListIsEmptyException()
            }

            val groups = groupStorage.getGroupEntities(groupIds)

            if (groups.size != groupIds.size) {
                throw GroupsErrorException()
            }

            for (group in groups) {
                if (group.ownerId != userId) {
                    throw MustBeGroupOwnerException()
                }
            }

            val deadline = request.deadline?.let {
                Instant.fromEpochSeconds(it)
            }

            val opensAt = request.deadline?.let {
                Instant.fromEpochSeconds(it)
            }

            if (deadline != null && deadline < Clock.System.now()) {
                throw DeadlineInPastException()
            }

            if (opensAt != null && deadline != null && opensAt >= deadline) {
                throw OpensAfterDeadlineException()
            }

            val res = lessonStorage.createLesson(
                name = request.name,
                description = request.description?.takeIf { it.isNotBlank() },
                isEstimatable = request.isEstimatable,
                deadline = deadline,
                opensAt = opensAt,
                groupIds = groupIds,
                authorId = userId
            )

            call.respond(
                HttpStatusCode.OK,
                res.toDto()
            )
        }
    }
}

@Serializable
data class CreateLessonRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("group_ids") val groupIds: List<String>,
    @SerialName("is_estimatable") val isEstimatable: Boolean = false,
    @SerialName("deadline") val deadline: Long? = null,
    @SerialName("opens_at") val opensAt: Long? = null,
)