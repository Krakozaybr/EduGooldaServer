package module.requests_and_invitations

import io.ktor.http.*
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.exception.NotBannedException
import itmo.edugoolda.api.group.route.v1.requests_and_invitations.UnbanUserRequest
import junit.framework.TestCase.assertTrue
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertFalse

class UnbanUserTests : ModuleTest {

    @Test
    fun test_unban_user_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        JoiningUtils.banStudent(groupId = groupId.toString(), studentId = student.userId)

        client.sendRequest(
            url = "/api/v1/group/unban",
            method = HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = UnbanUserRequest(
                userId = student.userId,
                groupId = groupId.toString()
            )
        ).expectOk()

        assertFalse(JoiningUtils.isBanned(groupId.toString(), student.userId))
    }

    @Test
    fun test_unban_user_already_unbanned() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        client.sendRequest(
            url = "/api/v1/group/unban",
            method = HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = UnbanUserRequest(
                userId = student.userId,
                groupId = groupId.toString()
            )
        ).expectError(HttpStatusCode.NotFound, NotBannedException.CODE)
    }

    @Test
    fun test_unban_user_not_owner() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        JoiningUtils.banStudent(groupId.toString(), student.userId)

        client.sendRequest(
            url = "/api/v1/group/unban",
            method = HttpMethod.Post,
            accessToken = student.accessToken,
            body = UnbanUserRequest(
                userId = student.userId,
                groupId = groupId.toString()
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)

        assertTrue(JoiningUtils.isBanned(groupId.toString(), student.userId))
    }
}