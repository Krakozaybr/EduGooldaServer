package module.requests_and_invitations

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.route.v1.requests_and_invitations.JoinRequestsResponse
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupJoinRequestsTests : ModuleTest {

    @Test
    fun test_group_join_requests_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val requests = List(10) {
            JoiningUtils.addJoinRequestStudent(
                groupId = groupId.toString(),
                studentId = client.registerStudent(
                    DefaultRegisterStudentRequest.copy(
                        email = "student$it@email.com"
                    )
                ).userId
            )
        }

        val body = client.sendRequest(
            url = "/api/v1/group/$groupId/join_requests?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<JoinRequestsResponse>()

        assertEquals(
            requests.size,
            body.total
        )

        assertEquals(
            requests.map { it.toString() }.toSet(),
            body.items.map { it.id }.toSet()
        )
    }

    @Test
    fun test_group_join_requests_pagination_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val requests = List(10) {
            JoiningUtils.addJoinRequestStudent(
                groupId = groupId.toString(),
                studentId = client.registerStudent(
                    DefaultRegisterStudentRequest.copy(
                        email = "student$it@email.com"
                    )
                ).userId
            )
        }

        val body1 = client.sendRequest(
            url = "/api/v1/group/$groupId/join_requests?page=1&page_size=5",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<JoinRequestsResponse>()

        val body2 = client.sendRequest(
            url = "/api/v1/group/$groupId/join_requests?page=2&page_size=5",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<JoinRequestsResponse>()

        assertEquals(
            5,
            body1.items.size
        )

        assertEquals(
            5,
            body2.items.size
        )

        assertEquals(
            requests.size,
            body1.total
        )

        assertEquals(
            requests.size,
            body2.total
        )

        assertEquals(
            requests.map { it.toString() }.toSet(),
            (body1.items + body2.items).map { it.id }.toSet()
        )
    }

    @Test
    fun test_group_join_requests_unknown_group() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        client.sendRequest(
            url = "/api/v1/group/${UUID.randomUUID()}/join_requests?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectError(HttpStatusCode.NotFound, GroupNotFoundException.CODE)
    }

    @Test
    fun test_group_join_requests_not_owner() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val requests = List(10) {
            JoiningUtils.addJoinRequestStudent(
                groupId = groupId.toString(),
                studentId = client.registerStudent(
                    DefaultRegisterStudentRequest.copy(
                        email = "student$it@email.com"
                    )
                ).userId
            )
        }

        client.sendRequest(
            url = "/api/v1/group/$groupId/join_requests?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)
    }
}