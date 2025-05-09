package itmo.edugoolda.api.group.route.v1.requests_and_invitations

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.error.exceptions.IdFormatException
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.exception.NotBannedException
import itmo.edugoolda.api.group.storage.ban.GroupBanStorage
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.EntityIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.unbanUserRoute(koin: Koin) {
    val groupStorage = koin.get<GroupStorage>()
    val banStorage = koin.get<GroupBanStorage>()

    authenticate {
        post<UnbanUserRequest>("/group/unban") {
            val userId = tokenContext?.userId ?: throw InvalidCredentialsException()

            val (groupId, studentId) = listOf(
                EntityIdentifier.parse(it.groupId) ?: throw IdFormatException("group_id"),
                EntityIdentifier.parse(it.userId) ?: throw IdFormatException("user_id")
            )

            val ownerId = groupStorage.getGroupInfo(groupId)?.ownerId
                ?: throw GroupNotFoundException(groupId)

            if (ownerId != userId) throw MustBeGroupOwnerException()

            if (!groupStorage.checkUserIsBanned(groupId = groupId, userId = studentId)) {
                throw NotBannedException(
                    studentId = studentId
                )
            }

            banStorage.unbanUser(groupId, studentId)

            call.respond(HttpStatusCode.OK)
        }
    }
}

@Serializable
data class UnbanUserRequest(
    @SerialName("student_id") val userId: String,
    @SerialName("group_id") val groupId: String,
)
