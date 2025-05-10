package module.group

import io.ktor.http.*
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import module.*
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test

class GroupDetailsTests : ModuleTest {
    @Test
    fun test_group_details_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val id = GroupUtils.createGroupInDatabase(
            ownerId = tokens.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(tokens.userId).toString()
        )

        client.sendRequest(
            url = "/api/v1/group/$id",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        ).expectOk()
    }

    @Test
    fun test_group_details_unknown() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        client.sendRequest(
            url = "/api/v1/group/${UUID.randomUUID()}",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        ).expectError(HttpStatusCode.NotFound, GroupNotFoundException.CODE)
    }
}