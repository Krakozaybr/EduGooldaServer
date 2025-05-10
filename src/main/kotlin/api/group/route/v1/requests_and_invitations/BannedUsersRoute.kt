package itmo.edugoolda.api.group.route.v1.requests_and_invitations

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

fun Route.bannedUsersRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()

    authenticate {
        get("/group/{$GROUP_ID_URL_PARAM}/banned") {
            val userId = tokenContext?.userId ?: throw InvalidCredentialsException()

            val groupId = idParameter(GROUP_ID_URL_PARAM)

            val group = groupStorage.getGroupEntity(groupId)
                ?: throw GroupNotFoundException(groupId)

            if (group.ownerId != userId) {
                throw MustBeGroupOwnerException()
            }

            val paging = call.queryParameters.parsePagination()

            val (banned, total) = groupStorage.getGroupBanned(
                skip = paging.skip,
                maxCount = paging.pageSize,
                groupId = groupId
            )

            call.respond(
                HttpStatusCode.OK,
                BannedUsersResponse(
                    total = total,
                    users = banned.map(UserInfoDto::from)
                )
            )
        }
    }
}

@Serializable
data class BannedUsersResponse(
    @SerialName("total") val total: Int,
    @SerialName("users") val users: List<UserInfoDto>
)