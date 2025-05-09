package module.group

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.route.v1.group.GroupStudentsResponse
import module.*
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupStudentsTests : ModuleTest {

    @Test
    fun test_group_students_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val studentIds = List(10) {
            client.registerStudent(
                request = DefaultRegisterStudentRequest.copy(
                    email = "teststudent$it@email.com"
                )
            ).userId
        }.toSet()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = tokens.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        studentIds.forEach {
            GroupUtils.addStudentToGroup(it, groupId.toString())
        }

        val body = client.sendRequest(
            url = "/api/v1/group/$groupId/students?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        ).expectOk().body<GroupStudentsResponse>()

        assertEquals(
            studentIds.size,
            body.total
        )

        assertEquals(
            studentIds,
            body.students.map { it.id }.toSet()
        )
    }

    @Test
    fun test_group_students_pagination_correct() = testJsonRequests { client ->
        val tokens = client.registerTeacher()

        val studentIds = List(10) {
            client.registerStudent(
                request = DefaultRegisterStudentRequest.copy(
                    email = "teststudent$it@email.com"
                )
            ).userId
        }.toSet()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = tokens.userId,
            subjectId = SubjectUtils.createSubjectInDatabase().toString()
        )

        studentIds.forEach {
            GroupUtils.addStudentToGroup(it, groupId.toString())
        }

        val body1 = client.sendRequest(
            url = "/api/v1/group/$groupId/students?page=1&page_size=5",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        ).expectOk().body<GroupStudentsResponse>()

        val body2 = client.sendRequest(
            url = "/api/v1/group/$groupId/students?page=2&page_size=5",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        ).expectOk().body<GroupStudentsResponse>()

        assertEquals(
            studentIds.size,
            body1.total
        )

        assertEquals(
            studentIds.size,
            body2.total
        )

        assertEquals(
            body1.students.size,
            5
        )

        assertEquals(
            body2.students.size,
            5
        )

        assertEquals(
            studentIds,
            (body1.students + body2.students).map { it.id }.toSet()
        )
    }
}