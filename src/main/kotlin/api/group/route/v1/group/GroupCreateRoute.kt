package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.error.exceptions.IdFormatException
import itmo.edugoolda.api.group.dto.GroupDetailsDto
import itmo.edugoolda.api.group.exception.GroupDescriptionException
import itmo.edugoolda.api.group.exception.GroupNameException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.utils.checkGroupDescription
import itmo.edugoolda.api.group.utils.checkGroupName
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.groupCreateRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()
    val userStorage = koin.get<UserStorage>()

    authenticate {
        post<GroupCreateRequest>("/group") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            if (user.role != UserRole.Teacher) {
                throw UnsuitableUserRoleException(UserRole.Teacher)
            }

            val subjectId = EntityIdentifier.parse(it.subjectId)
                ?: throw IdFormatException("subject_id")

            if (it.description != null && !checkGroupDescription(it.description)) {
                throw GroupDescriptionException()
            }

            if (!checkGroupName(it.name)) throw GroupNameException()

            val groupId = groupStorage.createGroup(
                name = it.name,
                description = it.description,
                subjectId = subjectId,
                ownerId = userId
            )

            val details = groupStorage.getGroupDetails(groupId)

            call.respond(
                HttpStatusCode.OK,
                GroupDetailsDto.from(details)
            )
        }
    }
}

@Serializable
data class GroupCreateRequest(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("subject_id") val subjectId: String,
)