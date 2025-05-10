package module.subject

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.route.v1.subject.SubjectsListResponse
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import module.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SubjectListTests : ModuleTest {

    @Test
    fun test_subject_list_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val expectedSubjects = List(10) {
            SubjectUtils.createSubjectInDatabase(
                ownerId = tokens.userId,
                name = "Subject$it"
            )
        }

        val resp = client.sendRequest(
            url = "/api/v1/subjects",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        )
        val body = resp.expectOk().body<SubjectsListResponse>()

        assertEquals(
            expectedSubjects.map { it.toString() }.toSet(),
            body.subjects.map { it.id }.toSet()
        )
    }

    @Test
    fun test_subject_list_empty_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()
        val teacher2 = client.registerTeacher(
            DefaultRegisterTeacherRequest.copy(
                email = "another@email.com"
            )
        )

        repeat(10) {
            SubjectUtils.createSubjectInDatabase(
                ownerId = teacher2.userId,
                name = "Subject$it"
            )
        }

        val resp = client.sendRequest(
            url = "/api/v1/subjects",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        )
        val body = resp.expectOk().body<SubjectsListResponse>()

        assertEquals(
            setOf(),
            body.subjects.map { it.id }.toSet()
        )
    }

    @Test
    fun test_subject_list_invalid_role() = testJsonRequests { client ->
        val tokens = client.registerStudent()

        val resp = client.sendRequest(
            url = "/api/v1/subjects",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        )

        resp.expectError(HttpStatusCode.Forbidden, UnsuitableUserRoleException.CODE)
    }
}