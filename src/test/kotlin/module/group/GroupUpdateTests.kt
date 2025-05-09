package module.group

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.dto.GroupDetailsDto
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.exception.SubjectNotFoundException
import itmo.edugoolda.api.group.route.v1.group.GroupUpdateRequest
import module.*
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupUpdateTests : ModuleTest {

    @Test
    fun test_group_update_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val group = GroupUtils.createGroupInDatabase(
            ownerId = tokens.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val request = GroupUpdateRequest(
            name = "newName",
            description = "newDescription",
            subjectId = SubjectUtils.createSubjectInDatabase().toString(),
            isActive = false
        )

        val body = client.sendRequest(
            url = "/api/v1/group/$group",
            method = HttpMethod.Put,
            accessToken = tokens.accessToken,
            body = request
        ).expectOk().body<GroupDetailsDto>()

        assertEquals(
            listOf(
                request.name,
                request.description,
                request.subjectId,
                request.isActive
            ),
            listOf(
                body.name,
                body.description,
                body.subject.id,
                body.isActive
            )
        )
    }

    @Test
    fun test_group_update_forbidden() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val tokens = client.registerStudent()

        val group = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val request = GroupUpdateRequest(
            name = "newName",
            description = "newDescription",
            subjectId = SubjectUtils.createSubjectInDatabase().toString(),
            isActive = false
        )

        client.sendRequest(
            url = "/api/v1/group/$group",
            method = HttpMethod.Put,
            accessToken = tokens.accessToken,
            body = request
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)
    }

    @Test
    fun test_group_update_not_found() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val request = GroupUpdateRequest(
            name = "newName",
            description = "newDescription",
            subjectId = SubjectUtils.createSubjectInDatabase().toString(),
            isActive = false
        )

        val body = client.sendRequest(
            url = "/api/v1/group/${UUID.randomUUID()}",
            method = HttpMethod.Put,
            accessToken = tokens.accessToken,
            body = request
        ).expectError(HttpStatusCode.NotFound, GroupNotFoundException.CODE)
    }

    @Test
    fun test_group_update_unknown_subject() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val group = GroupUtils.createGroupInDatabase(
            ownerId = tokens.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val request = GroupUpdateRequest(
            name = "newName",
            description = "newDescription",
            subjectId = UUID.randomUUID().toString(),
            isActive = false
        )

        val body = client.sendRequest(
            url = "/api/v1/group/$group",
            method = HttpMethod.Put,
            accessToken = tokens.accessToken,
            body = request
        ).expectError(HttpStatusCode.NotFound, SubjectNotFoundException.CODE)
    }
}