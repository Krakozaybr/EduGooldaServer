package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.dto.GroupDetailsDto
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.groupSetActiveRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()

    authenticate {
        put<GroupSetActiveRequest>("/group/{$GROUP_ID_URL_PARAM}/set_is_active") {
            // Checks and getting data
            val groupId = idParameter(GROUP_ID_URL_PARAM)

            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val groupInfo = groupStorage.getGroupEntity(groupId)
                ?: throw GroupNotFoundException(groupId)

            if (userId != groupInfo.ownerId) {
                throw MustBeGroupOwnerException()
            }

            // Main logic

            groupStorage.updateGroup(
                groupId = groupInfo.id,
                subjectId = groupInfo.subjectId,
                name = groupInfo.name,
                description = groupInfo.description,
                isActive = it.isActive
            )

            val details = groupStorage.getGroupDetails(groupId, userId)

            call.respond(
                HttpStatusCode.OK,
                GroupDetailsDto.from(details)
            )
        }
    }
}

@Serializable
data class GroupSetActiveRequest(
    @SerialName("is_active") val isActive: Boolean,
)