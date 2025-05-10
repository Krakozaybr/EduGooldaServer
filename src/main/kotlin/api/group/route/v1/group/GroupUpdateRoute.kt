package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.error.exceptions.IdFormatException
import itmo.edugoolda.api.group.dto.GroupDetailsDto
import itmo.edugoolda.api.group.exception.*
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.storage.subject.SubjectStorage
import itmo.edugoolda.api.group.utils.checkGroupDescription
import itmo.edugoolda.api.group.utils.checkGroupName
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.idParameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

private const val GROUP_ID_URL_PARAM = "group_id"

fun Route.groupUpdateRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()
    val subjectStorage = koin.get<SubjectStorage>()

    authenticate {
        put<GroupUpdateRequest>("/group/{$GROUP_ID_URL_PARAM}") {
            // Checks and getting data
            val groupId = idParameter(GROUP_ID_URL_PARAM)

            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val ownerId = groupStorage.getGroupEntity(groupId)
                ?.ownerId
                ?: throw GroupNotFoundException(groupId)

            if (userId != ownerId) {
                throw MustBeGroupOwnerException()
            }

            val subjectId = EntityIdentifier.parse(it.subjectId)
                ?: throw IdFormatException("subject_id")

            if (!subjectStorage.checkExists(subjectId)) {
                throw SubjectNotFoundException(subjectId)
            }

            if (!subjectStorage.checkIsUserOwner(subjectId = subjectId, userId = userId)) {
                throw MustBeSubjectOwnerException()
            }

            if (it.description != null && !checkGroupDescription(it.description)) {
                throw GroupDescriptionException()
            }

            if (!checkGroupName(it.name)) throw GroupNameException()

            // Main logic

            groupStorage.updateGroup(
                groupId = groupId,
                subjectId = subjectId,
                name = it.name,
                description = it.description,
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
data class GroupUpdateRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("subject_id") val subjectId: String,
    @SerialName("is_active") val isActive: Boolean,
)