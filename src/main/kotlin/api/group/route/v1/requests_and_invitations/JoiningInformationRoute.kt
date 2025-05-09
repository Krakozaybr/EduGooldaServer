package itmo.edugoolda.api.group.route.v1.requests_and_invitations

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.storage.group_request.GroupRequestStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.joiningInformationRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()
    val groupRequestStorage = koin.get<GroupRequestStorage>()

    authenticate {
        get("/group_invitation/{$GROUP_ID_URL_PARAM}") {
            val groupId = idParameter(GROUP_ID_URL_PARAM)

            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val ownerId = groupStorage.getGroupInfo(groupId)
                ?.ownerId
                ?: throw GroupNotFoundException(groupId)

            if (userId != ownerId) {
                throw MustBeGroupOwnerException()
            }

            val code = groupRequestStorage.generateCodeForGroup(groupId)

            call.respond(
                HttpStatusCode.OK,
                JoiningInformationResponse(
                    code = code,
                    link = "TODO"
                )
            )
        }
    }
}

@Serializable
data class JoiningInformationResponse(
    @SerialName("code") val code: String,
    @SerialName("invitation_link") val link: String
)
