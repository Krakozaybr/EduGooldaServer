package itmo.edugoolda.api.lessons.route.v1

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.lessons.exception.MessageNotFoundException
import itmo.edugoolda.api.lessons.exception.MustBeMessageAuthorException
import itmo.edugoolda.api.lessons.storage.lessons.LessonsStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val MESSAGE_ID_PATH_PARAMETER = "message_id"

fun Route.deleteMessageRoute(koin: Koin) {
    val lessonsStorage = koin.get<LessonsStorage>()

    authenticate {
        delete("/lesson_message/{$MESSAGE_ID_PATH_PARAMETER}") {
            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val messageId = idParameter(MESSAGE_ID_PATH_PARAMETER)

            val messageEntity = lessonsStorage.getMessageEntity(messageId)
                ?: throw MessageNotFoundException(messageId)

            if (messageEntity.authorId != userId) {
                throw MustBeMessageAuthorException()
            }

            lessonsStorage.deleteMessage(messageId)

            call.respond(HttpStatusCode.OK)
        }
    }
}