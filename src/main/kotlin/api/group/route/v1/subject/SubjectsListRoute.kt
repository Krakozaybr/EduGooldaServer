package itmo.edugoolda.api.group.route.v1.subject

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import itmo.edugoolda.api.group.dto.SubjectDto
import itmo.edugoolda.api.group.storage.subject.SubjectStorage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.Koin

fun Route.subjectsListRoute(koin: Koin) {
    val subjectStorage = koin.get<SubjectStorage>()

    authenticate {
        get("/subjects") {
            val list = subjectStorage.getSubjects()

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
