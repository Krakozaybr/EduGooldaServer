package itmo.edugoolda.api.group.route.v1.requests_and_invitations

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.error.exceptions.PrintableEnum
import itmo.edugoolda.api.group.domain.model.JoinRequestAction
import itmo.edugoolda.api.group.domain.model.JoinRequestStatus
import itmo.edugoolda.api.group.exception.*
import itmo.edugoolda.api.group.storage.ban.GroupBanStorage
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.storage.group_request.GroupRequestStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

private const val REQUEST_ID_URL_PARAM = "request_id"

fun Route.joinRequestActionRoute(koin: Koin) {
    val groupRequestStorage = koin.get<GroupRequestStorage>()
    val groupStorage = koin.get<GroupStorage>()
    val userStorage = koin.get<UserStorage>()
    val banStorage = koin.get<GroupBanStorage>()

    authenticate {
        put<JoinRequestActionRequest>("/join_request/{$REQUEST_ID_URL_PARAM}") {
            val userId = tokenContext?.userId ?: throw InvalidCredentialsException()

            val requestId = idParameter(REQUEST_ID_URL_PARAM)

            val action = PrintableEnum.parseOrThrow<JoinRequestAction>(it.action)

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            val request = groupRequestStorage.getJoinRequestInfoById(requestId)
                ?: throw JoinRequestNotFoundException(requestId)

            when (action) {
                JoinRequestAction.Accept,
                JoinRequestAction.Decline,
                JoinRequestAction.DeclineAndBan -> {
                    val checkIsGroupOwner = suspend {
                        val group = groupStorage.getGroupEntity(request.groupIdentifier)
                            ?: throw GroupNotFoundException(request.groupIdentifier)

                        group.ownerId == userId
                    }

                    if (user.role != UserRole.Teacher || !checkIsGroupOwner()) {
                        throw MustBeGroupOwnerException()
                    }
                }

                JoinRequestAction.Cancel -> {
                    if (request.senderId != userId) {
                        throw MustBeRequestSenderException()
                    }
                }
            }

            if (request.status != JoinRequestStatus.Pending) {
                throw RequestMustBePendingException()
            }

            when (action) {
                JoinRequestAction.Cancel -> {
                    groupRequestStorage.cancelAddToGroupRequest(requestId)
                }

                JoinRequestAction.Accept -> {
                    groupRequestStorage.acceptAddToGroupRequest(requestId)
                }

                JoinRequestAction.Decline,
                JoinRequestAction.DeclineAndBan -> {
                    groupRequestStorage.declineAddToGroupRequest(requestId)

                    if (action == JoinRequestAction.DeclineAndBan) {
                        banStorage.banUser(request.groupIdentifier, request.senderId)
                    }
                }
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}

@Serializable
data class JoinRequestActionRequest(
    @SerialName("action") val action: String
)
