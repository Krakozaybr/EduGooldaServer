package itmo.edugoolda.api.user.route.v1

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.user.domain.UserInfo
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.utils.EntityId

suspend fun RoutingContext.getEntityIdOr404(): EntityId? {
    val id = call.pathParameters[USER_ID_URL_PARAM]?.takeIf {
        it.isNotBlank()
    }?.let(EntityId::parse)

    if (id == null) {
        call.respond(HttpStatusCode.NotFound)
        return null
    }

    return id
}

suspend fun RoutingContext.getUserOr404(userStorage: UserStorage): UserInfo? {
    val id = getEntityIdOr404() ?: return null

    val user = userStorage.getUserById(id)

    if (user == null) {
        call.respond(
            HttpStatusCode.NotFound,
            ErrorResponse(
                errorCode = "USER_NOT_FOUND"
            )
        )
        return null
    }

    return user
}