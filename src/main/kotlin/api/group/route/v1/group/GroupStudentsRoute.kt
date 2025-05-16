package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.utils.parsePagination
import itmo.edugoolda.api.user.dto.UserInfoDto
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.groupStudentsRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()

    authenticate {
        get("/group/{$GROUP_ID_URL_PARAM}/students") {
            val groupId = idParameter(GROUP_ID_URL_PARAM)

            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val groupInfo = groupStorage.getGroupEntity(groupId)
                ?: throw GroupNotFoundException(groupId)

            if (userId != groupInfo.ownerId) throw MustBeGroupOwnerException()

            val params = call.queryParameters.parsePagination()

            val (students, total) = groupStorage.getGroupStudents(
                skip = params.skip,
                maxCount = params.pageSize,
                groupId = groupId
            )

            call.respond(
                HttpStatusCode.OK,
                GroupStudentsResponse(
                    total = total,
                    students = students.map(UserInfoDto::from)
                )
            )
        }
    }
}

@Serializable
data class GroupStudentsResponse(
    @SerialName("users") val students: List<UserInfoDto>,
    @SerialName("total") val total: Int,
)