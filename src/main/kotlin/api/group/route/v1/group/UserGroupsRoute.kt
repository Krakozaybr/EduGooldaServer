package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.error.exceptions.IdFormatException
import itmo.edugoolda.api.group.dto.GroupInfoDto
import itmo.edugoolda.api.group.dto.GroupsListParams
import itmo.edugoolda.api.group.exception.SubjectNotFoundException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.storage.subject.SubjectStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.userGroupsRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()
    val userStorage = koin.get<UserStorage>()
    val subjectStorage = koin.get<SubjectStorage>()

    authenticate {
        get("/groups") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val params = GroupsListParams.from(call.queryParameters)

            val subjectId = when (val id = params.subjectId) {
                null -> null
                else -> {
                    val subjectId = id.let(EntityIdentifier::parse) ?: throw IdFormatException("subject_id")

                    if (!subjectStorage.checkExists(subjectId)) {
                        throw SubjectNotFoundException(subjectId)
                    }

                    subjectId
                }
            }

            val role = userStorage.getUserById(userId)?.role
                ?: throw UserNotFoundException(userId)

            val (groups, total) = when (role) {
                UserRole.Teacher -> groupStorage.getTeacherGroups(
                    skip = params.paginationDto.skip,
                    maxCount = params.paginationDto.pageSize,
                    userId = userId,
                    query = params.query,
                    subjectId = subjectId
                )

                UserRole.Student -> groupStorage.getStudentGroups(
                    skip = params.paginationDto.skip,
                    maxCount = params.paginationDto.pageSize,
                    userId = userId,
                    query = params.query,
                    subjectId = subjectId
                )
            }

            call.respond(
                HttpStatusCode.OK,
                UserGroupsResponse(
                    total = total,
                    groups = groups.map(GroupInfoDto::from)
                )
            )
        }
    }
}

@Serializable
data class UserGroupsResponse(
    @SerialName("groups") val groups: List<GroupInfoDto>,
    @SerialName("total") val total: Int,
)