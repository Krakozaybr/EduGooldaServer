package itmo.edugoolda.api.group.route.v1.requests_and_invitations

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.domain.use_case.SendJoinRequestUseCase
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.joinGroupByLinkRoute(koin: Koin) {
    val joinUseCase = koin.get<SendJoinRequestUseCase>()

    authenticate {
        post("/group/{$GROUP_ID_URL_PARAM}/join") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val groupId = idParameter(GROUP_ID_URL_PARAM)

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