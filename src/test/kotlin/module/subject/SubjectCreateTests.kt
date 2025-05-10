package module.subject

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.dto.SubjectDto
import itmo.edugoolda.api.group.exception.SubjectAlreadyExistsException
import itmo.edugoolda.api.group.route.v1.subject.SubjectCreateRequest
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import module.*
import kotlin.test.Test

class SubjectCreateTests : ModuleTest {
    @Test
    fun test_subject_create_correct() = testJsonRequests { client ->

        val teacher = client.registerUniqueTeacher()
        val teacher2 = client.registerUniqueTeacher()
        val request = SubjectCreateRequest(name = faker.funnyName.name())

        client.sendRequest(
            url = "/api/v1/subject",
            method = HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = request
        ).expectOk().body<SubjectDto>()

        client.sendRequest(
            url = "/api/v1/subject",
            method = HttpMethod.Post,
            accessToken = teacher2.accessToken,
            body = request
        ).expectOk().body<SubjectDto>()
    }

    @Test
    fun test_subject_create_already_exists() = testJsonRequests { client ->

        val teacher = client.registerUniqueTeacher()
        val request = SubjectCreateRequest(name = faker.funnyName.name())

        client.sendRequest(
            url = "/api/v1/subject",
            method = HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = request
        ).expectOk().body<SubjectDto>()

        client.sendRequest(
            url = "/api/v1/subject",
            method = HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = request
        ).expectError(HttpStatusCode.BadRequest, SubjectAlreadyExistsException.CODE)
    }

    @Test
    fun test_subject_create_by_student() = testJsonRequests { client ->

        val teacher = client.registerUniqueStudent()

        client.sendRequest(
            url = "/api/v1/subject",
            method = HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = SubjectCreateRequest(name = faker.funnyName.name())
        ).expectError(HttpStatusCode.Forbidden, UnsuitableUserRoleException.CODE)
    }
}