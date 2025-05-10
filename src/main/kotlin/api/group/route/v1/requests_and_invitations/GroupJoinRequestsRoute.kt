package itmo.edugoolda.api.group.route.v1.requests_and_invitations

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.dto.JoinRequestDto
import itmo.edugoolda.api.group.dto.toDTO
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.storage.group_request.GroupRequestStorage
import itmo.edugoolda.api.group.utils.parsePagination
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.groupJoinRequestsRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()
    val groupRequestStorage = koin.get<GroupRequestStorage>()

    authenticate {
        get("/group/{$GROUP_ID_URL_PARAM}/join_requests") {
            val userId = tokenContext?.userId ?: throw InvalidCredentialsException()

            val groupId = idParameter(GROUP_ID_URL_PARAM)

            val ownerId = groupStorage.getGroupEntity(groupId)
                ?.ownerId
                ?: throw GroupNotFoundException(groupId)

            if (userId != ownerId) {
                throw MustBeGroupOwnerException()
            }

            val pagingParams = call.request.queryParameters.parsePagination()

            val (requests, total) = groupRequestStorage.getGroupJoinRequests(
                groupId = groupId,
                skip = pagingParams.skip,
                limit = pagingParams.pageSize
            )

            call.respond(
                HttpStatusCode.OK,
                JoinRequestsResponse(
                    items = requests.map { it.toDTO() },
                    total = total
                )
            )
        }
    }
}

@Serializable
data class JoinRequestsResponse(
    @SerialName("requests") val items: List<JoinRequestDto>,
    @SerialName("total") val total: Int
)