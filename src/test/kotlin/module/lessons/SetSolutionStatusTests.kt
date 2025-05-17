package module.lessons

import io.ktor.http.*
import itmo.edugoolda.api.lessons.domain.SolutionStatus
import itmo.edugoolda.api.lessons.exception.MustBeLessonAuthorException
import itmo.edugoolda.api.lessons.exception.SolutionNotFoundException
import itmo.edugoolda.api.lessons.route.v1.SetSolutionStatusRequest
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SetSolutionStatusTests : ModuleTest {

    @Test
    fun test_set_status_correct() = testJsonRequests { client ->
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

        client.sendRequest(
            url = "/api/v1/solution/${solutionId}/status",
            HttpMethod.Put,
            accessToken = teacher.accessToken,
            body = SetSolutionStatusRequest(
                status = SolutionStatus.Reviewed.string
            )
        ).expectOk()

        assertEquals(
            SolutionStatus.Reviewed,
            LessonsUtils.getSolutionStatus(solutionId.toString())
        )
    }

    @Test
    fun test_set_status_forbidden() = testJsonRequests { client ->
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

        client.sendRequest(
            url = "/api/v1/solution/${solutionId}/status",
            HttpMethod.Put,
            accessToken = student.accessToken,
            body = SetSolutionStatusRequest(
                status = SolutionStatus.Reviewed.string
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeLessonAuthorException.CODE)
    }

    @Test
    fun test_set_status_not_found() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()

        client.sendRequest(
            url = "/api/v1/solution/${UUID.randomUUID()}/status",
            HttpMethod.Put,
            accessToken = teacher.accessToken,
            body = SetSolutionStatusRequest(
                status = SolutionStatus.Reviewed.string
            )
        ).expectError(HttpStatusCode.NotFound, SolutionNotFoundException.CODE)
    }
}