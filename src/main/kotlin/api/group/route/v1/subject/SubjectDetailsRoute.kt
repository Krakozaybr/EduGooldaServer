package itmo.edugoolda.api.group.route.v1.subject

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.group.dto.SubjectDto
import itmo.edugoolda.api.group.exception.SubjectNotFoundException
import itmo.edugoolda.api.group.storage.subject.SubjectStorage
import itmo.edugoolda.utils.idParameter
import org.koin.core.Koin

private const val SUBJECT_ID_URL_PARAM = "subject_id"

fun Route.subjectDetailsRoute(koin: Koin) {
    val subjectStorage = koin.get<SubjectStorage>()

    authenticate {
        get("/subjects/{$SUBJECT_ID_URL_PARAM}") {
            val subjectId = idParameter(SUBJECT_ID_URL_PARAM)

            val details = subjectStorage.getById(subjectId)
                ?: throw SubjectNotFoundException(subjectId)

            call.respond(
                HttpStatusCode.OK,
                SubjectDto.from(details)
            )
        }
    }
}