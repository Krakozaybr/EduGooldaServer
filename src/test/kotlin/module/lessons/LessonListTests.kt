package module.lessons

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.lessons.dto.LessonFullDetailsDTO
import itmo.edugoolda.api.lessons.route.v1.CreateLessonRequest
import itmo.edugoolda.api.lessons.route.v1.LessonsListResponse
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class LessonListTests : ModuleTest {

    @Test
    fun test_lesson_list_for_student() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val student = client.registerUniqueStudent()

        val subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        val groupId1 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )

        GroupUtils.addStudentToGroup(
            studentId = student.userId,
            groupId = groupId1.toString()
        )

        val lessonId = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString()),
        )

        val lessonId2 = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString()),
        )

        val body = client.sendRequest(
            url = "/api/v1/lessons?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<LessonsListResponse>()

        assertEquals(
            2,
            body.total
        )

        assertEquals(
            2,
            body.items.size
        )

        assertEquals(
            setOf(lessonId.toString(), lessonId2.toString()),
            body.items.map { it.id }.toSet()
        )
    }

    @Test
    fun test_lesson_list_and_create_for_student() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val student = client.registerUniqueStudent()

        val subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        val groupId1 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )

        GroupUtils.addStudentToGroup(
            studentId = student.userId,
            groupId = groupId1.toString()
        )

        val lessonDto = client.sendRequest(
            url = "/api/v1/lesson",
            method = HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "Lesson",
                description = null,
                groupIds = listOf(groupId1.toString()),
                isEstimatable = false,
            )
        ).expectOk().body<LessonFullDetailsDTO>()

        val body = client.sendRequest(
            url = "/api/v1/lessons?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<LessonsListResponse>()

        assertEquals(
            1,
            body.total
        )

        assertEquals(
            1,
            body.items.size
        )

        assertEquals(
            setOf(lessonDto.id),
            body.items.map { it.id }.toSet()
        )
    }

    @Test
    fun test_lesson_list_for_student_specified_group_id() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val student = client.registerUniqueStudent()

        val subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        val groupId1 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )
        val groupId2 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )

        GroupUtils.addStudentToGroup(
            studentId = student.userId,
            groupId = groupId1.toString()
        )

        val lessonId = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString()),
        )

        val lessonId2 = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId2.toString()),
        )

        val body = client.sendRequest(
            url = "/api/v1/lessons?page=1&page_size=10&group_id=${groupId1}",
            method = HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<LessonsListResponse>()

        assertEquals(
            1,
            body.total
        )

        assertEquals(
            1,
            body.items.size
        )

        assertEquals(
            setOf(lessonId.toString()),
            body.items.map { it.id }.toSet()
        )
    }

    @Test
    fun test_lesson_list_for_teacher() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()

        val subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        val groupId1 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )

        val lessonId = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString()),
        )

        val lessonId2 = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString()),
        )

        val body = client.sendRequest(
            url = "/api/v1/lessons?page=1&page_size=10",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<LessonsListResponse>()

        assertEquals(
            2,
            body.total
        )

        assertEquals(
            2,
            body.items.size
        )

        assertEquals(
            setOf(lessonId.toString(), lessonId2.toString()),
            body.items.map { it.id }.toSet()
        )
    }

    @Test
    fun test_lesson_list_for_teacher_group_specified() = testJsonRequests { client ->
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

        val lessonId = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString()),
        )

        val lessonId2 = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId2.toString()),
        )

        val body = client.sendRequest(
            url = "/api/v1/lessons?page=1&page_size=10&group_id=${groupId1}",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<LessonsListResponse>()

        assertEquals(
            1,
            body.total
        )

        assertEquals(
            1,
            body.items.size
        )

        assertEquals(
            setOf(lessonId.toString()),
            body.items.map { it.id }.toSet()
        )
    }
}