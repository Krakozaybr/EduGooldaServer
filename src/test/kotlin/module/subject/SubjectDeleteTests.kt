package module.subject

import io.ktor.http.*
import itmo.edugoolda.api.group.exception.MustBeSubjectOwnerException
import itmo.edugoolda.api.group.exception.SubjectNotFoundException
import module.*
import java.util.*
import kotlin.test.Test

class SubjectDeleteTests : ModuleTest {

    @Test
    fun test_subject_delete_correct() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()

        val id = SubjectUtils.createSubjectInDatabase(ownerId = teacher.userId)

        client.sendRequest(
            url = "/api/v1/subject/${id}",
            method = HttpMethod.Delete,
            accessToken = teacher.accessToken,
        ).expectOk()
    }

    @Test
    fun test_subject_delete_unknown() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()

        client.sendRequest(
            url = "/api/v1/subject/${UUID.randomUUID()}",
            method = HttpMethod.Delete,
            accessToken = teacher.accessToken,
        ).expectError(HttpStatusCode.NotFound, SubjectNotFoundException.CODE)
    }

    @Test
    fun test_subject_delete_not_owner() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val teacher2 = client.registerUniqueTeacher()

        val id = SubjectUtils.createSubjectInDatabase(ownerId = teacher.userId)

        client.sendRequest(
            url = "/api/v1/subject/${id}",
            method = HttpMethod.Delete,
            accessToken = teacher2.accessToken,
        ).expectError(HttpStatusCode.Forbidden, MustBeSubjectOwnerException.CODE)
    }
}