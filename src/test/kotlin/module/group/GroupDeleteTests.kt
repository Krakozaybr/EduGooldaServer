package module.group

import io.ktor.http.*
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import module.*
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test

class GroupDeleteTests : ModuleTest {

    @Test
    fun test_group_delete_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val id = GroupUtils.createGroupInDatabase(
            ownerId = tokens.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        client.sendRequest(
            url = "/api/v1/group/$id",
            method = HttpMethod.Delete,
            accessToken = tokens.accessToken
        ).expectOk()
    }

    @Test
    fun test_group_delete_not_owner() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val id = GroupUtils.createGroupInDatabase(
            ownerId = UUID.randomUUID().toString(),
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        client.sendRequest(
            url = "/api/v1/group/$id",
            method = HttpMethod.Delete,
            accessToken = tokens.accessToken
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)
    }

    @Test
    fun test_group_delete_unknown() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        client.sendRequest(
            url = "/api/v1/group/${UUID.randomUUID()}",
            method = HttpMethod.Delete,
            accessToken = tokens.accessToken
        ).expectError(HttpStatusCode.NotFound, GroupNotFoundException.CODE)
    }
}