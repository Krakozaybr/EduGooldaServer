package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.groupSetFavourite(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()

    authenticate {
        put<GroupSetFavouriteRequest>("/group/{$GROUP_ID_URL_PARAM}/set_is_favourite") {
            val userId = tokenContext?.userId ?: throw InvalidCredentialsException()

            val groupId = idParameter(GROUP_ID_URL_PARAM)

            if (groupStorage.getGroupEntity(groupId) == null) {
                throw GroupNotFoundException(groupId)
            }

            groupStorage.setIsFavourite(
                userId = userId,
                groupId = groupId,
                isFavourite = it.isFavourite
            )

            call.respond(HttpStatusCode.OK)
        }
    }
}

@Serializable
data class GroupSetFavouriteRequest(
    @SerialName("is_favourite") val isFavourite: Boolean
)
