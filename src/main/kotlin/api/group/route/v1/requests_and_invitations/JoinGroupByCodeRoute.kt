package itmo.edugoolda.api.group.route.v1.requests_and_invitations

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.domain.use_case.SendJoinRequestUseCase
import itmo.edugoolda.api.group.exception.UnknownCodeException
import itmo.edugoolda.api.group.storage.group_request.GroupRequestStorage
import itmo.edugoolda.plugins.tokenContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.joinGroupByCodeRoute(koin: Koin) {

    val groupRequestStorage = koin.get<GroupRequestStorage>()
    val joinUseCase = koin.get<SendJoinRequestUseCase>()

    authenticate {
        post<JoinGroupByCodeRequest>("/group/join") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val groupId = groupRequestStorage.getGroupIdByCode(it.code)
                ?: throw UnknownCodeException(it.code)

            joinUseCase.invoke(
                userId = userId,
                groupId = groupId
            )

            call.respond(
                HttpStatusCode.OK,
                JoinGroupResponse(groupId = groupId.stringValue)
            )
        }
    }
}

@Serializable
data class JoinGroupByCodeRequest(
    @SerialName("code") val code: String
)

@Serializable
data class JoinGroupResponse(
    @SerialName("group_id") val groupId: String
)