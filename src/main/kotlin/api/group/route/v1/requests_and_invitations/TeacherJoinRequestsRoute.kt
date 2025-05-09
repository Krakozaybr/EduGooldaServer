package itmo.edugoolda.api.group.route.v1.requests_and_invitations

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.dto.toDTO
import itmo.edugoolda.api.group.storage.group_request.GroupRequestStorage
import itmo.edugoolda.api.group.utils.parsePagination
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.plugins.tokenContext
import org.koin.core.Koin

fun Route.teacherJoinRequestsRoute(koin: Koin) {
    val groupRequestStorage = koin.get<GroupRequestStorage>()
    val userStorage = koin.get<UserStorage>()

    authenticate {
        get("/join_requests") {
            val userId = tokenContext?.userId ?: throw InvalidCredentialsException()

            val user = userStorage.getUserById(userId)
                ?: throw UserNotFoundException(userId)

            if (user.role != UserRole.Teacher) {
                throw UnsuitableUserRoleException(UserRole.Teacher)
            }

            val params = call.queryParameters.parsePagination()

            val (requests, total) = groupRequestStorage.getTeacherJoinRequests(
                userId = userId,
                skip = params.skip,
                limit = params.pageSize,
            )

            call.respond(
                HttpStatusCode.OK,
                JoinRequestsResponse(
                    items = requests.map { it.toDTO() },
                    total = total
                )
            )
        }
    }
}