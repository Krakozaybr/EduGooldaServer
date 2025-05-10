package itmo.edugoolda.api.group.route.v1.group

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.error.exceptions.IdFormatException
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.exception.NotParticipantException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.groupRemoveStudentRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()

    authenticate {
        post<GroupRemoveStudentRequest>("/group/kick") {

            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val groupId = it.groupId.let(EntityIdentifier::parse)
                ?: throw IdFormatException("group_id")

            val studentId = it.studentId.let(EntityIdentifier::parse)
                ?: throw IdFormatException("student_id")

            val ownerId = groupStorage.getGroupEntity(groupId)
                ?.ownerId
                ?: throw GroupNotFoundException(groupId)

            if (userId != ownerId) {
                throw MustBeGroupOwnerException()
            }

            val wasDeleted = groupStorage.removeUsersFromGroup(
                id = studentId,
                groupId = groupId
            )

            if (!wasDeleted) {
                throw NotParticipantException(
                    studentId = studentId,
                    groupId = groupId
                )
            }

            call.respond(HttpStatusCode.OK)
        }
    }
}

@Serializable
data class GroupRemoveStudentRequest(
    @SerialName("action") val action: String,
    @SerialName("group_id") val groupId: String,
    @SerialName("student_id") val studentId: String,
)