package itmo.edugoolda.api.group.route.v1.subject

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.auth.exception.InvalidCredentialsException
import itmo.edugoolda.api.group.exception.MustBeSubjectOwnerException
import itmo.edugoolda.api.group.exception.SubjectNotFoundException
import itmo.edugoolda.api.group.storage.subject.SubjectStorage
import itmo.edugoolda.plugins.tokenContext
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val SUBJECT_ID_URL_PARAM = "subject_id"

fun Route.deleteSubjectRoute(koin: Koin) {
    val subjectStorage = koin.get<SubjectStorage>()

    authenticate {
        delete("/subject/{$SUBJECT_ID_URL_PARAM}") {

            val userId = tokenContext?.userId
                ?: throw InvalidCredentialsException()

            val subjectId = idParameter(SUBJECT_ID_URL_PARAM)

            val subjectOwnerId = subjectStorage.getById(subjectId)?.ownerId
                ?: throw SubjectNotFoundException(subjectId)

            if (subjectOwnerId != userId) {
                throw MustBeSubjectOwnerException()
            }

            subjectStorage.deleteSubject(subjectId)

            call.respond(HttpStatusCode.OK)
        }
    }
}