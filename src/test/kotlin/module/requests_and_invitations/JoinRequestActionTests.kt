package module.requests_and_invitations

import io.ktor.http.*
import itmo.edugoolda.api.group.domain.model.JoinRequestAction
import itmo.edugoolda.api.group.domain.model.JoinRequestStatus
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.exception.MustBeRequestSenderException
import itmo.edugoolda.api.group.route.v1.requests_and_invitations.JoinRequestActionRequest
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JoinRequestActionTests : ModuleTest {

    @Test
    fun test_join_request_action_accept_by_teacher_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val requestId = JoiningUtils.addJoinRequestStudent(
            groupId = groupId.toString(),
            studentId = student.userId
        )

        client.sendRequest(
            url = "/api/v1/join_request/${requestId}",
            method = HttpMethod.Put,
            accessToken = teacher.accessToken,
            body = JoinRequestActionRequest(
                action = JoinRequestAction.Accept.string
            )
        ).expectOk()

        assertEquals(
            JoinRequestStatus.Accepted,
            JoiningUtils.getJoinRequestStatus(requestId.toString())
        )
    }

    @Test
    fun test_join_request_action_decline_by_teacher_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val requestId = JoiningUtils.addJoinRequestStudent(
            groupId = groupId.toString(),
            studentId = student.userId
        )

        client.sendRequest(
            url = "/api/v1/join_request/${requestId}",
            method = HttpMethod.Put,
            accessToken = teacher.accessToken,
            body = JoinRequestActionRequest(
                action = JoinRequestAction.Decline.string
            )
        ).expectOk()

        assertEquals(
            JoinRequestStatus.Declined,
            JoiningUtils.getJoinRequestStatus(requestId.toString())
        )
    }

    @Test
    fun test_join_request_action_decline_and_ban_by_teacher_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val requestId = JoiningUtils.addJoinRequestStudent(
            groupId = groupId.toString(),
            studentId = student.userId
        )

        client.sendRequest(
            url = "/api/v1/join_request/${requestId}",
            method = HttpMethod.Put,
            accessToken = teacher.accessToken,
            body = JoinRequestActionRequest(
                action = JoinRequestAction.DeclineAndBan.string
            )
        ).expectOk()

        assertEquals(
            JoinRequestStatus.Declined,
            JoiningUtils.getJoinRequestStatus(requestId.toString())
        )

        assertTrue(JoiningUtils.isBanned(groupId.toString(), student.userId))
    }

    @Test
    fun test_join_request_action_cancel_by_teacher_error() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val requestId = JoiningUtils.addJoinRequestStudent(
            groupId = groupId.toString(),
            studentId = student.userId
        )

        client.sendRequest(
            url = "/api/v1/join_request/${requestId}",
            method = HttpMethod.Put,
            accessToken = teacher.accessToken,
            body = JoinRequestActionRequest(
                action = JoinRequestAction.Cancel.string
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeRequestSenderException.CODE)
    }

    @Test
    fun test_join_request_action_accept_by_student_error() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val requestId = JoiningUtils.addJoinRequestStudent(
            groupId = groupId.toString(),
            studentId = student.userId
        )

        client.sendRequest(
            url = "/api/v1/join_request/${requestId}",
            method = HttpMethod.Put,
            accessToken = student.accessToken,
            body = JoinRequestActionRequest(
                action = JoinRequestAction.Accept.string
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)
    }

    @Test
    fun test_join_request_action_decline_by_student_error() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val requestId = JoiningUtils.addJoinRequestStudent(
            groupId = groupId.toString(),
            studentId = student.userId
        )

        client.sendRequest(
            url = "/api/v1/join_request/${requestId}",
            method = HttpMethod.Put,
            accessToken = student.accessToken,
            body = JoinRequestActionRequest(
                action = JoinRequestAction.Decline.string
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)
    }

    @Test
    fun test_join_request_action_decline_and_ban_by_student_error() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val requestId = JoiningUtils.addJoinRequestStudent(
            groupId = groupId.toString(),
            studentId = student.userId
        )

        client.sendRequest(
            url = "/api/v1/join_request/${requestId}",
            method = HttpMethod.Put,
            accessToken = student.accessToken,
            body = JoinRequestActionRequest(
                action = JoinRequestAction.DeclineAndBan.string
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)

        assertFalse(JoiningUtils.isBanned(groupId.toString(), student.userId))
    }

    @Test
    fun test_join_request_action_cancel_by_student_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        val requestId = JoiningUtils.addJoinRequestStudent(
            groupId = groupId.toString(),
            studentId = student.userId
        )

        client.sendRequest(
            url = "/api/v1/join_request/${requestId}",
            method = HttpMethod.Put,
            accessToken = student.accessToken,
            body = JoinRequestActionRequest(
                action = JoinRequestAction.Cancel.string
            )
        ).expectOk()

        assertEquals(
            JoinRequestStatus.Cancelled,
            JoiningUtils.getJoinRequestStatus(requestId.toString())
        )
    }
}