package module.group

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.domain.model.RemoveStudentAction
import itmo.edugoolda.api.group.dto.GroupDetailsDto
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.group.exception.NotParticipantException
import itmo.edugoolda.api.group.route.v1.group.GroupRemoveStudentRequest
import module.*
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupRemoveStudentTests : ModuleTest {
    @Test
    fun test_group_remove_student_correct() = testJsonRequests { client ->
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
            url = "/api/v1/group/kick",
            method = HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = GroupRemoveStudentRequest(
                action = RemoveStudentAction.Kick.string,
                groupId = groupId.toString(),
                studentId = student.userId
            )
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
    fun test_group_remove_student_forbidden() = testJsonRequests { client ->
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
            url = "/api/v1/group/kick",
            method = HttpMethod.Post,
            accessToken = student.accessToken,
            body = GroupRemoveStudentRequest(
                action = RemoveStudentAction.Kick.string,
                groupId = groupId.toString(),
                studentId = student.userId
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)

        val details = client.sendRequest(
            url = "/api/v1/group/$groupId",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<GroupDetailsDto>()

        assertEquals(
            expected = 1,
            actual = details.studentsCount
        )
    }

    @Test
    fun test_group_remove_student_not_participant() = testJsonRequests { client ->
        val student = client.registerStudent()
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        client.sendRequest(
            url = "/api/v1/group/kick",
            method = HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = GroupRemoveStudentRequest(
                action = RemoveStudentAction.Kick.string,
                groupId = groupId.toString(),
                studentId = student.userId
            )
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