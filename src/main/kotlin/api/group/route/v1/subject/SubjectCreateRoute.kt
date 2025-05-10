package itmo.edugoolda.api.group.route.v1.subject

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.dto.SubjectDto
import itmo.edugoolda.api.group.exception.SubjectAlreadyExistsException
import itmo.edugoolda.api.group.storage.subject.SubjectStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.createSubjectRoute(koin: Koin) {
    val subjectStorage = koin.get<SubjectStorage>()
    val userStorage = koin.get<UserStorage>()

    authenticate {
        post<SubjectCreateRequest>("/subject") {

            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val userRole = userStorage.getUserById(userId)?.role
                ?: throw UserNotFoundException(userId)

            if (userRole != UserRole.Teacher) {
                throw UnsuitableUserRoleException(UserRole.Teacher)
            }

            if (subjectStorage.checkExists(it.name, userId)) {
                throw SubjectAlreadyExistsException(it.name)
            }

            val subject = subjectStorage.createSubject(it.name, userId)

            call.respond(
                HttpStatusCode.OK,
                SubjectDto.from(subject)
            )
        }
    }
}

@Serializable
data class SubjectCreateRequest(
    @SerialName("name") val name: String
)
