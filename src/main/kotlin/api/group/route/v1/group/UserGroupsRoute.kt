package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.dto.GroupInfoDto
import itmo.edugoolda.api.group.dto.GroupsListParams
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.userGroupsRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()
    val userStorage = koin.get<UserStorage>()

    authenticate {
        get("/groups") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val params = GroupsListParams.from(call.queryParameters)

            val role = userStorage.getUserById(userId)?.role
                ?: throw UserNotFoundException(userId)

            val (groups, total) = when (role) {
                UserRole.Teacher -> groupStorage.getTeacherGroups(
                    skip = params.paginationDto.skip,
                    maxCount = params.paginationDto.pageSize,
                    userId = userId,
                    query = params.query,
                    subjectName = params.subjectName
                )

                UserRole.Student -> groupStorage.getStudentGroups(
                    skip = params.paginationDto.skip,
                    maxCount = params.paginationDto.pageSize,
                    userId = userId,
                    query = params.query,
                    subjectName = params.subjectName
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