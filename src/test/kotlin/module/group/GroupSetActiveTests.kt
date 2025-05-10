package module.group

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.dto.GroupDetailsDto
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.route.v1.group.GroupSetActiveRequest
import module.*
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupSetActiveTests : ModuleTest {

    @Test
    fun test_group_set_active_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val id = GroupUtils.createGroupInDatabase(
            ownerId = tokens.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(tokens.userId).toString()
        )

        var body = client.sendRequest(
            url = "/api/v1/group/$id/set_is_active",
            method = HttpMethod.Put,
            accessToken = tokens.accessToken,
            body = GroupSetActiveRequest(
                isActive = true
            )
        ).expectOk().body<GroupDetailsDto>()

        assertEquals(
            true,
            body.isActive
        )

        body = client.sendRequest(
            url = "/api/v1/group/$id/set_is_active",
            method = HttpMethod.Put,
            accessToken = tokens.accessToken,
            body = GroupSetActiveRequest(
                isActive = false
            )
        ).expectOk().body<GroupDetailsDto>()

        assertEquals(
            false,
            body.isActive
        )
    }

    @Test
    fun test_group_set_active_not_owner() = testJsonRequests { client ->
        val tokens = client.registerTeacher()
        val student = client.registerStudent()

        val id = GroupUtils.createGroupInDatabase(
            ownerId = tokens.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(tokens.userId).toString()
        )

        client.sendRequest(
            url = "/api/v1/group/$id/set_is_active",
            method = HttpMethod.Put,
            accessToken = student.accessToken,
            body = GroupSetActiveRequest(
                isActive = true
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)
    }
}