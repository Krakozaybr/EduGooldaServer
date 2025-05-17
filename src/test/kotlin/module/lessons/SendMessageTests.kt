package module.lessons

import io.ktor.http.*
import itmo.edugoolda.api.group.exception.MustBeParticipantException
import itmo.edugoolda.api.lessons.exception.MustBeLessonAuthorException
import itmo.edugoolda.api.lessons.exception.SolutionMessageException
import itmo.edugoolda.api.lessons.route.v1.SendMessageRequest
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SendMessageTests : ModuleTest {

    @Test
    fun test_send_message_by_student() = testJsonRequests { client ->
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

        client.sendRequest(
            url = "/api/v1/lesson/${lessonId}/message",
            HttpMethod.Post,
            accessToken = student.accessToken,
            body = SendMessageRequest(
                message = "message"
            )
        ).expectOk()

        val solutionId = LessonsUtils.getSolution(
            lessonId = lessonId.toString(),
            userId = student.userId.toString()
        )

        assertNotNull(solutionId)

        val messages = LessonsUtils.getMessages(
            solutionId = solutionId.toString(),
            userId = student.userId
        )

        assertEquals(
            1,
            messages.size
        )
    }

    @Test
    fun test_send_message_by_student_too_long() = testJsonRequests { client ->
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

        client.sendRequest(
            url = "/api/v1/lesson/${lessonId}/message",
            HttpMethod.Post,
            accessToken = student.accessToken,
            body = SendMessageRequest(
                message = "message".repeat(300_000)
            )
        ).expectError(HttpStatusCode.BadRequest, SolutionMessageException.CODE)
    }

    @Test
    fun test_send_message_twice_by_student() = testJsonRequests { client ->
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

        client.sendRequest(
            url = "/api/v1/lesson/${lessonId}/message",
            HttpMethod.Post,
            accessToken = student.accessToken,
            body = SendMessageRequest(
                message = "message"
            )
        ).expectOk()

        client.sendRequest(
            url = "/api/v1/lesson/${lessonId}/message",
            HttpMethod.Post,
            accessToken = student.accessToken,
            body = SendMessageRequest(
                message = "message"
            )
        ).expectOk()

        val solutionId = LessonsUtils.getSolution(
            lessonId = lessonId.toString(),
            userId = student.userId.toString()
        )

        assertNotNull(solutionId)

        val messages = LessonsUtils.getMessages(
            solutionId = solutionId.toString(),
            userId = student.userId
        )

        assertEquals(
            2,
            messages.size
        )
    }

    @Test
    fun test_send_message_by_student_and_teacher() = testJsonRequests { client ->
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

        client.sendRequest(
            url = "/api/v1/lesson/${lessonId}/message",
            HttpMethod.Post,
            accessToken = student.accessToken,
            body = SendMessageRequest(
                message = "message"
            )
        ).expectOk()

        val solutionId = LessonsUtils.getSolution(
            lessonId = lessonId.toString(),
            userId = student.userId.toString()
        )

        client.sendRequest(
            url = "/api/v1/solution/${solutionId}/message",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = SendMessageRequest(
                message = "message"
            )
        ).expectOk()

        assertNotNull(solutionId)

        val messages = LessonsUtils.getMessages(
            solutionId = solutionId.toString()
        )

        assertEquals(
            2,
            messages.size
        )
    }

    @Test
    fun test_send_message_by_student_and_teacher_too_long() = testJsonRequests { client ->
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

        client.sendRequest(
            url = "/api/v1/lesson/${lessonId}/message",
            HttpMethod.Post,
            accessToken = student.accessToken,
            body = SendMessageRequest(
                message = "message"
            )
        ).expectOk()

        val solutionId = LessonsUtils.getSolution(
            lessonId = lessonId.toString(),
            userId = student.userId.toString()
        )

        client.sendRequest(
            url = "/api/v1/solution/${solutionId}/message",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = SendMessageRequest(
                message = "message".repeat(300_000)
            )
        ).expectError(HttpStatusCode.BadRequest, SolutionMessageException.CODE)
    }

    @Test
    fun test_send_message_by_teacher_like_student() = testJsonRequests { client ->
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

        client.sendRequest(
            url = "/api/v1/lesson/${lessonId}/message",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = SendMessageRequest(
                message = "message"
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeParticipantException.CODE)
    }

    @Test
    fun test_send_message_by_student_like_teacher() = testJsonRequests { client ->
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

        client.sendRequest(
            url = "/api/v1/lesson/${lessonId}/message",
            HttpMethod.Post,
            accessToken = student.accessToken,
            body = SendMessageRequest(
                message = "message"
            )
        ).expectOk()

        val solutionId = LessonsUtils.getSolution(
            lessonId = lessonId.toString(),
            userId = student.userId.toString()
        )

        client.sendRequest(
            url = "/api/v1/solution/${solutionId}/message",
            HttpMethod.Post,
            accessToken = student.accessToken,
            body = SendMessageRequest(
                message = "message"
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeLessonAuthorException.CODE)
    }
}