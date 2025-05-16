package module.group

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.dto.GroupDetailsDto
import itmo.edugoolda.api.group.exception.GroupNotFoundException
import itmo.edugoolda.api.group.exception.NotParticipantException
import module.*
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupLeaveTests : ModuleTest {
    @Test
    fun test_group_leave_correct() = testJsonRequests { client ->
        val student = client.registerStudent()
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        GroupUtils.addStudentToGroup(
            groupId = groupId.toString(),
            studentId = student.userId
        )

        client.sendRequest(
            url = "/api/v1/group/${groupId}/leave",
            method = HttpMethod.Post,
            accessToken = student.accessToken,
        ).expectOk()

        val details = client.sendRequest(
            url = "/api/v1/group/$groupId",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<GroupDetailsDto>()

        assertEquals(
            expected = 0,
            actual = details.studentsCount
        )
    }

    @Test
    fun test_group_leave_not_found() = testJsonRequests { client ->
        val student = client.registerStudent()

        client.sendRequest(
            url = "/api/v1/group/${UUID.randomUUID()}/leave",
            method = HttpMethod.Post,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.NotFound, GroupNotFoundException.CODE)
    }

    @Test
    fun test_group_leave_not_participant() = testJsonRequests { client ->
        val student = client.registerStudent()
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        client.sendRequest(
            url = "/api/v1/group/$groupId/leave",
            method = HttpMethod.Post,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.BadRequest, NotParticipantException.CODE)

        val details = client.sendRequest(
            url = "/api/v1/group/$groupId",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<GroupDetailsDto>()

        assertEquals(
            expected = 0,
            actual = details.studentsCount
        )
    }
}