package module.group

import io.ktor.http.*
import itmo.edugoolda.api.group.route.v1.group.GroupSetFavouriteRequest
import module.*
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupSetFavouriteTests : ModuleTest {
    @Test
    fun test_teacher_set_favourite_true_is_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        client.sendRequest(
            url = "/api/v1/group/$groupId/set_is_favourite",
            method = HttpMethod.Put,
            accessToken = teacher.accessToken,
            body = GroupSetFavouriteRequest(true)
        ).expectOk()

        assertEquals(
            true,
            GroupUtils.getFavourite(userId = teacher.userId, groupId = groupId.toString())
        )
    }

    @Test
    fun test_teacher_set_favourite_false_is_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        client.sendRequest(
            url = "/api/v1/group/$groupId/set_is_favourite",
            method = HttpMethod.Put,
            accessToken = teacher.accessToken,
            body = GroupSetFavouriteRequest(false)
        ).expectOk()

        assertEquals(
            false,
            GroupUtils.getFavourite(userId = teacher.userId, groupId = groupId.toString())
        )
    }

    @Test
    fun test_student_set_favourite_true_is_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        client.sendRequest(
            url = "/api/v1/group/$groupId/set_is_favourite",
            method = HttpMethod.Put,
            accessToken = student.accessToken,
            body = GroupSetFavouriteRequest(true)
        ).expectOk()

        assertEquals(
            true,
            GroupUtils.getFavourite(userId = student.userId, groupId = groupId.toString())
        )
    }

    @Test
    fun test_student_set_favourite_false_is_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        client.sendRequest(
            url = "/api/v1/group/$groupId/set_is_favourite",
            method = HttpMethod.Put,
            accessToken = student.accessToken,
            body = GroupSetFavouriteRequest(false)
        ).expectOk()

        assertEquals(
            false,
            GroupUtils.getFavourite(userId = student.userId, groupId = groupId.toString())
        )
    }
}