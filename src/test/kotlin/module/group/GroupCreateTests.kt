package module.group

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.group.dto.GroupDetailsDto
import itmo.edugoolda.api.group.dto.SubjectDto
import itmo.edugoolda.api.group.exception.GroupDescriptionException
import itmo.edugoolda.api.group.exception.GroupNameException
import itmo.edugoolda.api.group.exception.MustBeSubjectOwnerException
import itmo.edugoolda.api.group.exception.SubjectNotFoundException
import itmo.edugoolda.api.group.route.v1.group.GroupCreateRequest
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import itmo.edugoolda.utils.toCurrentLocalDateTime
import kotlinx.datetime.Clock
import module.*
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupCreateTests : ModuleTest {

    @Test
    fun test_createGroup_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val subjectId = SubjectUtils.createSubjectInDatabase(tokens.userId)

        val expectedResponse = GroupDetailsDto(
            id = "",
            name = "Test group",
            description = null,
            owner = client.sendRequest(
                url = "/api/v1/user/${tokens.userId}",
                method = HttpMethod.Get,
                accessToken = tokens.accessToken
            ).body(),
            subject = SubjectDto(
                name = SubjectUtils.DefaultSubjectName,
                id = subjectId.toString()
            ),
            studentsCount = 0,
            requestsCount = 0,
            bannedCount = 0,
            newSolutionsCount = 0,
            tasksCount = 0,
            isActive = true,
            createdAt = Clock.System.now().toCurrentLocalDateTime(),
            isFavourite = false
        )

        val body = client.sendRequest(
            url = "/api/v1/group",
            method = HttpMethod.Post,
            accessToken = tokens.accessToken,
            body = GroupCreateRequest(
                name = "Test group",
                subjectId = subjectId.toString(),
                description = null
            )
        ).expectOk().body<GroupDetailsDto>()

        assertEquals(
            expectedResponse.copy(
                id = body.id,
                createdAt = body.createdAt
            ),
            body
        )
    }

    @Test
    fun test_createGroup_not_teacher() = testJsonRequests { client ->
        val tokens = client.registerStudent()

        val subjectId = SubjectUtils.createSubjectInDatabase(tokens.userId)

        val body = client.sendRequest(
            url = "/api/v1/group",
            method = HttpMethod.Post,
            accessToken = tokens.accessToken,
            body = GroupCreateRequest(
                name = "Test group",
                subjectId = subjectId.toString(),
                description = null
            )
        ).expectCode(HttpStatusCode.Forbidden).body<ErrorResponse>()

        assertEquals(
            expected = UnsuitableUserRoleException.CODE,
            actual = body.errorCode
        )
    }

    @Test
    fun test_createGroup_unknown_subject() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        client.sendRequest(
            url = "/api/v1/group",
            method = HttpMethod.Post,
            accessToken = tokens.accessToken,
            body = GroupCreateRequest(
                name = "Test group",
                subjectId = UUID.randomUUID().toString(),
                description = null
            )
        ).expectError(HttpStatusCode.NotFound, SubjectNotFoundException.CODE)
    }

    @Test
    fun test_createGroup_not_subject_owner() = testJsonRequests { client ->
        val tokens = client.registerTeacher()
        val teacher2 = client.registerTeacher(
            DefaultRegisterTeacherRequest.copy(
                email = "another@email.com"
            )
        )

        val subjectId = SubjectUtils.createSubjectInDatabase(teacher2.userId)

        client.sendRequest(
            url = "/api/v1/group",
            method = HttpMethod.Post,
            accessToken = tokens.accessToken,
            body = GroupCreateRequest(
                name = "Test group",
                subjectId = subjectId.toString(),
                description = null
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeSubjectOwnerException.CODE)
    }

    @Test
    fun test_createGroup_description_error_too_long() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        client.sendRequest(
            url = "/api/v1/group",
            method = HttpMethod.Post,
            accessToken = tokens.accessToken,
            body = GroupCreateRequest(
                name = "Test group",
                subjectId = SubjectUtils.createSubjectInDatabase(tokens.userId).toString(),
                description = "zdf".repeat(300)
            )
        ).expectError(HttpStatusCode.BadRequest, GroupDescriptionException.CODE)
    }

    @Test
    fun test_createGroup_description_error_empty() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val subjectId = SubjectUtils.createSubjectInDatabase(tokens.userId)

        client.sendRequest(
            url = "/api/v1/group",
            method = HttpMethod.Post,
            accessToken = tokens.accessToken,
            body = GroupCreateRequest(
                name = "",
                subjectId = subjectId.toString(),
                description = "zdf".repeat(300)
            )
        ).expectError(HttpStatusCode.BadRequest, GroupDescriptionException.CODE)
    }

    @Test
    fun test_createGroup_name_error_empty() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val subjectId = SubjectUtils.createSubjectInDatabase(tokens.userId)

        client.sendRequest(
            url = "/api/v1/group",
            method = HttpMethod.Post,
            accessToken = tokens.accessToken,
            body = GroupCreateRequest(
                name = "",
                subjectId = subjectId.toString(),
                description = null
            )
        ).expectError(HttpStatusCode.BadRequest, GroupNameException.CODE)
    }

    @Test
    fun test_createGroup_name_error_too_long() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val subjectId = SubjectUtils.createSubjectInDatabase(tokens.userId)

        client.sendRequest(
            url = "/api/v1/group",
            method = HttpMethod.Post,
            accessToken = tokens.accessToken,
            body = GroupCreateRequest(
                name = "sdf".repeat(300),
                subjectId = subjectId.toString(),
                description = null
            )
        ).expectError(HttpStatusCode.BadRequest, GroupNameException.CODE)
    }
}
