package module.group

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.route.v1.group.UserGroupsResponse
import module.*
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class UserGroupsTests : ModuleTest {
    @Test
    fun test_student_groups_correct() = testJsonRequests { client ->
        val student = client.registerStudent()
        val teacher = client.registerTeacher()

        val groups = List(10) {
            val groupId = GroupUtils.createGroupInDatabase(
                ownerId = teacher.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
            )

            GroupUtils.addStudentToGroup(student.userId, groupId.toString())

            groupId
        }

        val body = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            groups.size,
            body.total
        )

        assertEquals(
            groups.map { it.toString() }.toSet(),
            body.groups.map { it.id }.toSet()
        )
    }

    @Test
    fun test_student_groups_empty_correct() = testJsonRequests { client ->
        val student = client.registerStudent()
        val teacher = client.registerTeacher()

        val groups = List(10) {
            val groupId = GroupUtils.createGroupInDatabase(
                ownerId = teacher.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
            )

            groupId
        }

        val body = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            0,
            body.total
        )

        assertEquals(
            setOf(),
            body.groups.map { it.id }.toSet()
        )
    }

    @Test
    fun test_student_groups_subject_search_correct() = testJsonRequests { client ->
        val student = client.registerStudent()
        val teacher = client.registerTeacher()

        val searchIndex = 4
        val subjectName = "subject$searchIndex"

        val expected = List(10) { index ->
            val groupId = GroupUtils.createGroupInDatabase(
                ownerId = teacher.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(
                    name = "subject$index",
                    ownerId = teacher.userId
                ).toString()
            )

            GroupUtils.addStudentToGroup(student.userId, groupId.toString())

            groupId
        }

        val body = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=10&subject_query=$subjectName",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            expected.size,
            body.total
        )

        assertEquals(
            subjectName,
            body.groups.firstOrNull()?.subjectName
        )
    }

    @Test
    fun test_student_groups_pagination_correct() = testJsonRequests { client ->
        val student = client.registerStudent()
        val teacher = client.registerTeacher()

        val groups = List(10) {
            val groupId = GroupUtils.createGroupInDatabase(
                ownerId = teacher.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
            )

            GroupUtils.addStudentToGroup(student.userId, groupId.toString())

            groupId
        }

        val body1 = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=5",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            body1.total,
            groups.size
        )

        val body2 = client.sendRequest(
            url = "/api/v1/groups?page=2&page_size=5",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            body1.total,
            groups.size
        )

        assertEquals(
            groups.map { it.toString() }.toSet(),
            (body1.groups + body2.groups).map { it.id }.toSet()
        )
    }

    @Test
    fun test_student_groups_name_search_correct() = testJsonRequests { client ->
        val student = client.registerStudent()
        val teacher = client.registerTeacher()

        var searchName = ""

        val groups = List(10) { index ->
            val groupId = GroupUtils.createGroupInDatabase(
                name = "group$index".also {
                    if (index == 4) {
                        searchName = it
                    }
                },
                ownerId = teacher.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
            )

            GroupUtils.addStudentToGroup(student.userId, groupId.toString())

            groupId
        }

        val body = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=10&query=$searchName",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            groups.size,
            body.total
        )

        assertEquals(
            searchName,
            body.groups.firstOrNull()?.name
        )
    }

    @Test
    fun test_teacher_groups_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val groups = List(10) {
            GroupUtils.createGroupInDatabase(
                ownerId = teacher.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
            )
        }

        val body = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            body.total,
            groups.size
        )

        assertEquals(
            groups.map { it.toString() }.toSet(),
            body.groups.map { it.id }.toSet()
        )
    }

    @Test
    fun test_teacher_groups_empty_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val teacher2 = client.registerTeacher(
            request = DefaultRegisterTeacherRequest.copy(
                email = "someanother@email.com"
            )
        )

        val groups = List(10) {
            GroupUtils.createGroupInDatabase(
                ownerId = teacher2.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
            )
        }

        val body = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            0,
            body.total
        )

        assertEquals(
            setOf(),
            body.groups.map { it.id }.toSet()
        )
    }

    @Test
    fun test_teacher_groups_subject_search_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val searchIndex = 4
        val subjectName = "subject$searchIndex"

        val expected = List(10) { index ->
            val groupId = GroupUtils.createGroupInDatabase(
                ownerId = teacher.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(
                    name = "subject$index",
                    ownerId = teacher.userId
                ).toString()
            )

            groupId
        }

        val body = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=10&subject_query=$subjectName",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            expected.size,
            body.total
        )

        assertEquals(
            subjectName,
            body.groups.firstOrNull()?.subjectName
        )
    }

    @Test
    fun test_teacher_groups_pagination_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val groups = List(10) {
            val groupId = GroupUtils.createGroupInDatabase(
                ownerId = teacher.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
            )

            groupId
        }

        val body1 = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=5",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            body1.total,
            groups.size
        )

        val body2 = client.sendRequest(
            url = "/api/v1/groups?page=2&page_size=5",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            body1.total,
            groups.size
        )

        assertEquals(
            groups.map { it.toString() }.toSet(),
            (body1.groups + body2.groups).map { it.id }.toSet()
        )
    }

    @Test
    fun test_teacher_groups_name_search_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        var searchName = ""

        val groups = List(10) { index ->
            val groupId = GroupUtils.createGroupInDatabase(
                name = "group$index".also {
                    if (index == 4) {
                        searchName = it
                    }
                },
                ownerId = teacher.userId,
                subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
            )
            groupId
        }

        val body = client.sendRequest(
            url = "/api/v1/groups?page=1&page_size=10&query=$searchName",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<UserGroupsResponse>()

        assertEquals(
            groups.size,
            body.total
        )

        assertEquals(
            searchName,
            body.groups.firstOrNull()?.name
        )
    }
}