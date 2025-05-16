package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.NotParticipantException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.groupLeaveRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()

    authenticate {
        post("/group/{$GROUP_ID_URL_PARAM}/leave") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val groupId = idParameter(GROUP_ID_URL_PARAM)

            if (groupStorage.getGroupEntity(groupId) == null) {
                throw GroupNotFoundException(groupId)
            }

            if (!groupStorage.checkStudentIsParticipant(groupId = groupId, userId = userId)) {
                throw NotParticipantException(studentId = userId, groupId = groupId)
            }

            groupStorage.removeUsersFromGroup(groupId = groupId, id = userId)

            call.respond(HttpStatusCode.OK)
        }
    }
}