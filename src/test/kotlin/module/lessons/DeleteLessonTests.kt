package module.lessons

import io.ktor.http.*
import itmo.edugoolda.api.lessons.exception.LessonNotFoundException
import itmo.edugoolda.api.lessons.exception.MustBeLessonAuthorException
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test
import kotlin.test.assertFalse

class DeleteLessonTests : ModuleTest {
    @Test
    fun test_deletion_correct() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()

        val subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        val groupId1 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )
        val groupId2 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )

        val id = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString(), groupId2.toString()),
        )

        client.sendRequest(
            "/api/v1/lesson/$id",
            HttpMethod.Delete,
            accessToken = teacher.accessToken
        ).expectOk()

        assertFalse(LessonsUtils.checkLessonExists(id.toString()))
    }

    @Test
    fun test_deletion_not_owner() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val teacher2 = client.registerUniqueTeacher()

        val subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        val groupId1 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )
        val groupId2 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )

        val id = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString(), groupId2.toString()),
        )

        client.sendRequest(
            "/api/v1/lesson/$id",
            HttpMethod.Delete,
            accessToken = teacher2.accessToken
        ).expectError(HttpStatusCode.Forbidden, MustBeLessonAuthorException.CODE)
    }

    @Test
    fun test_deletion_not_found() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()

        client.sendRequest(
            "/api/v1/lesson/${UUID.randomUUID()}",
            HttpMethod.Delete,
            accessToken = teacher.accessToken
        ).expectError(HttpStatusCode.NotFound, LessonNotFoundException.CODE)
    }
}