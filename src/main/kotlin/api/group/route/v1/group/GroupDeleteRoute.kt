package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.groupDeleteRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()

    authenticate {
        delete("/group/{$GROUP_ID_URL_PARAM}") {
            val groupId = idParameter(GROUP_ID_URL_PARAM)

            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val ownerId = groupStorage.getGroupEntity(groupId)
                ?.ownerId
                ?: throw GroupNotFoundException(groupId)

            if (userId != ownerId) {
                throw MustBeGroupOwnerException()
            }

            groupStorage.deleteGroup(groupId)

            call.respond(HttpStatusCode.OK)
        }
    }
}
