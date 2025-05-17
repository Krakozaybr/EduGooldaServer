package module.lessons

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.lessons.domain.SolutionStatus
import itmo.edugoolda.api.lessons.dto.LessonFullDetailsDTO
import itmo.edugoolda.api.lessons.dto.LessonStudentDetailsDTO
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class LessonDetailsTests : ModuleTest {

    @Test
    fun test_lesson_details_by_student_correct() = testJsonRequests { client ->
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

        val messageId1 = LessonsUtils.createMessage(
            solutionId = solutionId.toString(),
            author = student.userId,
            text = "ololo1"
        )

        val messageId2 = LessonsUtils.createMessage(
            solutionId = solutionId.toString(),
            author = teacher.userId,
            text = "ololo2"
        )

        val body = client.sendRequest(
            url = "/api/v1/lesson/$lessonId",
            HttpMethod.Get,
            accessToken = student.accessToken
        ).expectOk().body<LessonStudentDetailsDTO>()

        assertEquals(
            2,
            body.messages.size
        )
        assertEquals(
            "ololo1" to messageId1.toString(),
            body.messages[0].message to body.messages[0].id
        )
        assertEquals(
            "ololo2" to messageId2.toString(),
            body.messages[1].message to body.messages[1].id
        )
        assertEquals(
            lessonId.toString(),
            body.id
        )
        assertEquals(
            teacher.userId,
            body.teacher.id
        )
        assertEquals(
            SolutionStatus.Pending.string,
            body.status
        )
    }

    @Test
    fun test_lesson_details_by_teacher_correct() = testJsonRequests { client ->
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

        val messageId1 = LessonsUtils.createMessage(
            solutionId = solutionId.toString(),
            author = student.userId,
            text = "ololo1"
        )

        val messageId2 = LessonsUtils.createMessage(
            solutionId = solutionId.toString(),
            author = teacher.userId,
            text = "ololo2"
        )

        val body = client.sendRequest(
            url = "/api/v1/lesson/$lessonId",
            HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<LessonFullDetailsDTO>()

        assertEquals(
            lessonId.toString(),
            body.id
        )
        assertEquals(
            teacher.userId,
            body.teacher.id
        )
        assertEquals(
            1,
            body.solutionsCount
        )
    }
}