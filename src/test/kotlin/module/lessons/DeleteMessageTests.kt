package module.lessons

import io.ktor.http.*
import itmo.edugoolda.api.lessons.exception.MustBeMessageAuthorException
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertFalse

class DeleteMessageTests : ModuleTest {

    @Test
    fun test_deletion_by_student_correct() = testJsonRequests { client ->
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

        val messageId = LessonsUtils.createMessage(
            solutionId = solutionId.toString(),
            author = student.userId,
            text = "ololo"
        )

        client.sendRequest(
            url = "/api/v1/lesson_message/${messageId}",
            HttpMethod.Delete,
            accessToken = student.accessToken,
        ).expectOk()

        assertFalse(LessonsUtils.checkMessageExists(messageId.toString()))
    }

    @Test
    fun test_deletion_by_teacher_correct() = testJsonRequests { client ->
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

        val messageId = LessonsUtils.createMessage(
            solutionId = solutionId.toString(),
            author = teacher.userId,
            text = "ololo"
        )

        client.sendRequest(
            url = "/api/v1/lesson_message/${messageId}",
            HttpMethod.Delete,
            accessToken = teacher.accessToken,
        ).expectOk()

        assertFalse(LessonsUtils.checkMessageExists(messageId.toString()))
    }

    @Test
    fun test_deletion_by_student_not_owner() = testJsonRequests { client ->
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

        val messageId = LessonsUtils.createMessage(
            solutionId = solutionId.toString(),
            author = teacher.userId,
            text = "ololo"
        )

        client.sendRequest(
            url = "/api/v1/lesson_message/${messageId}",
            HttpMethod.Delete,
            accessToken = student.accessToken,
        ).expectError(HttpStatusCode.Forbidden, MustBeMessageAuthorException.CODE)
    }

    @Test
    fun test_deletion_by_teacher_not_owner() = testJsonRequests { client ->
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

        val messageId = LessonsUtils.createMessage(
            solutionId = solutionId.toString(),
            author = student.userId,
            text = "ololo"
        )

        client.sendRequest(
            url = "/api/v1/lesson_message/${messageId}",
            HttpMethod.Delete,
            accessToken = teacher.accessToken,
        ).expectError(HttpStatusCode.Forbidden, MustBeMessageAuthorException.CODE)
    }
}