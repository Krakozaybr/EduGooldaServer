package module.lessons

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.lessons.dto.SolutionDetailsDTO
import itmo.edugoolda.api.lessons.exception.MustBeLessonAuthorException
import itmo.edugoolda.api.lessons.exception.SolutionNotFoundException
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SolutionDetailsTests : ModuleTest {

    @Test
    fun test_solution_details_correct() = testJsonRequests { client ->
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
            groups = listOf(groupId1.toString(), groupId2.toString()),
        )

        val solutionId = LessonsUtils.createSolution(
            lessonId = lessonId.toString(),
            author = student.userId,
            teacherId = teacher.userId
        )

        val body = client.sendRequest(
            url = "/api/v1/solution/${solutionId}",
            HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<SolutionDetailsDTO>()

        assertEquals(
            solutionId.toString(),
            body.id
        )

        assertEquals(
            student.userId.toString(),
            body.author.id
        )

        assertEquals(
            lessonId.toString(),
            body.lesson.id
        )

        assertEquals(
            teacher.userId.toString(),
            body.lesson.teacher.id
        )
    }

    @Test
    fun test_solution_details_forbidden() = testJsonRequests { client ->
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
            groups = listOf(groupId1.toString(), groupId2.toString()),
        )

        val solutionId = LessonsUtils.createSolution(
            lessonId = lessonId.toString(),
            author = student.userId,
            teacherId = teacher.userId
        )

        val body = client.sendRequest(
            url = "/api/v1/solution/${solutionId}",
            HttpMethod.Get,
            accessToken = student.accessToken
        ).expectError(HttpStatusCode.Forbidden, MustBeLessonAuthorException.CODE)
    }

    @Test
    fun test_solution_details_deleted_lesson() = testJsonRequests { client ->
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
            groups = listOf(groupId1.toString(), groupId2.toString()),
        )

        val solutionId = LessonsUtils.createSolution(
            lessonId = lessonId.toString(),
            author = student.userId,
            teacherId = teacher.userId
        )

        LessonsUtils.deleteLesson(lessonId.toString())

        client.sendRequest(
            url = "/api/v1/solution/${solutionId}",
            HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectError(HttpStatusCode.NotFound, SolutionNotFoundException.CODE)
    }

    @Test
    fun test_solution_not_found() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()

        client.sendRequest(
            url = "/api/v1/solution/${UUID.randomUUID()}",
            HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectError(HttpStatusCode.NotFound, SolutionNotFoundException.CODE)
    }
}