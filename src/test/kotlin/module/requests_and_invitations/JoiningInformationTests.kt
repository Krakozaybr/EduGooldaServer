package module.requests_and_invitations

import io.ktor.http.*
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test

class JoiningInformationTests : ModuleTest {

    @Test
    fun test_joining_information_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        client.sendRequest(
            url = "/api/v1/group_invitation/$groupId",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk()
    }

    @Test
    fun test_joining_information_not_owner() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        client.sendRequest(
            url = "/api/v1/group_invitation/$groupId",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)
    }

    @Test
    fun test_joining_information_unknown() = testJsonRequests { client ->
        val student = client.registerStudent()

        client.sendRequest(
            url = "/api/v1/group_invitation/${UUID.randomUUID()}",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.NotFound, GroupNotFoundException.CODE)
    }
}