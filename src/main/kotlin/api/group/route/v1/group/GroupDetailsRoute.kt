package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.group.dto.GroupDetailsDto
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.groupDetailsRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()

    authenticate {
        get("/group/{$GROUP_ID_URL_PARAM}") {
            val groupId = idParameter(GROUP_ID_URL_PARAM)

            val details = groupStorage.getGroupDetails(groupId)

            call.respond(
                HttpStatusCode.OK,
                GroupDetailsDto.from(details)
            )
        }
    }
}