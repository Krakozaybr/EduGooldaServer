package module.requests_and_invitations

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.route.v1.requests_and_invitations.BannedUsersResponse
import junit.framework.TestCase.assertEquals
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test

class BannedUsersTests : ModuleTest {

    @Test
    fun test_banned_users_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val bannedUsers = List(10) {
            val student = client.registerStudent(
                DefaultRegisterStudentRequest.copy(
                    email = "student$it@email.com"
                )
            )

            JoiningUtils.banStudent(
                groupId = groupId.toString(),
                studentId = student.userId
            )

            student.userId
        }

        val body = client.sendRequest(
            url = "/api/v1/group/$groupId/banned?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<BannedUsersResponse>()

        assertEquals(
            bannedUsers.size,
            body.total
        )

        assertEquals(
            bannedUsers.toSet(),
            body.users.map { it.id }.toSet()
        )
    }

    @Test
    fun test_banned_users_pagination_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val bannedUsers = List(10) {
            val student = client.registerStudent(
                DefaultRegisterStudentRequest.copy(
                    email = "student$it@email.com"
                )
            )

            JoiningUtils.banStudent(
                groupId = groupId.toString(),
                studentId = student.userId
            )

            student.userId
        }

        val body1 = client.sendRequest(
            url = "/api/v1/group/$groupId/banned?page=1&page_size=5",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<BannedUsersResponse>()

        val body2 = client.sendRequest(
            url = "/api/v1/group/$groupId/banned?page=2&page_size=5",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<BannedUsersResponse>()

        assertEquals(
            bannedUsers.size,
            body1.total
        )

        assertEquals(
            bannedUsers.size,
            body2.total
        )

        assertEquals(
            5,
            body2.users.size
        )

        assertEquals(
            5,
            body1.users.size
        )

        assertEquals(
            bannedUsers.toSet(),
            (body1.users + body2.users).map { it.id }.toSet()
        )
    }

    @Test
    fun test_banned_users_unknown_group() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        client.sendRequest(
            url = "/api/v1/group/${UUID.randomUUID()}/banned?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectError(HttpStatusCode.NotFound, GroupNotFoundException.CODE)
    }

    @Test
    fun test_banned_users_not_owner() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        List(10) {
            val student = client.registerStudent(
                DefaultRegisterStudentRequest.copy(
                    email = "student$it@email.com"
                )
            )

            JoiningUtils.banStudent(
                groupId = groupId.toString(),
                studentId = student.userId
            )

            student.userId
        }

        val body = client.sendRequest(
            url = "/api/v1/group/$groupId/banned?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)
    }
}