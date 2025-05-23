package itmo.edugoolda.api.group.route.v1.subject

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.dto.SubjectDto
import itmo.edugoolda.api.group.storage.subject.SubjectStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.subjectsListRoute(koin: Koin) {
    val subjectStorage = koin.get<SubjectStorage>()
    val userStorage = koin.get<UserStorage>()

    authenticate {
        get("/subjects") {

            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val userRole = userStorage.getUserById(userId)?.role
                ?: throw UserNotFoundException(userId)

            if (userRole != UserRole.Teacher) {
                throw UnsuitableUserRoleException(UserRole.Teacher)
            }

            val list = subjectStorage.getSubjects(userId)

            call.respond(
                HttpStatusCode.OK,
                SubjectsListResponse(list.map { SubjectDto.from(it) })
            )
        }
    }
}

@Serializable
data class SubjectsListResponse(
    @SerialName("subjects") val subjects: List<SubjectDto>
)
