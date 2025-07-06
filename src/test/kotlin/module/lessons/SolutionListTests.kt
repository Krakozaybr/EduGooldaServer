package module.lessons

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.lessons.domain.SolutionStatus
import itmo.edugoolda.api.lessons.route.v1.SolutionListResponse
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class SolutionListTests : ModuleTest {

    @Test
    fun test_solution_list() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val student = client.registerUniqueStudent()
        val student2 = client.registerUniqueStudent()

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
        GroupUtils.addStudentToGroup(
            studentId = student2.userId,
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

        val solutionId2 = LessonsUtils.createSolution(
            lessonId = lessonId.toString(),
            author = student2.userId,
            teacherId = teacher.userId,
        )

        val body = client.sendRequest(
            url = "/api/v1/solutions?lessonId=$lessonId&page_size=10&page=1",
            HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<SolutionListResponse>()

        assertEquals(
            2,
            body.total
        )

        assertEquals(
            2,
            body.items.size
        )
    }

    @Test
    fun test_solution_list_with_status() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val student = client.registerUniqueStudent()
        val student2 = client.registerUniqueStudent()

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
        GroupUtils.addStudentToGroup(
            studentId = student2.userId,
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

        val solutionId2 = LessonsUtils.createSolution(
            lessonId = lessonId.toString(),
            author = student2.userId,
            teacherId = teacher.userId,
            status = SolutionStatus.Reviewed
        )

        val body = client.sendRequest(
            url = "/api/v1/solutions?lessonId=$lessonId&page_size=10&page=1&status=${SolutionStatus.Reviewed.string}",
            HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<SolutionListResponse>()

        assertEquals(
            1,
            body.total
        )

        assertEquals(
            1,
            body.items.size
        )
    }

    @Test
    fun test_solution_list_with_group() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val student = client.registerUniqueStudent()
        val student2 = client.registerUniqueStudent()

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
        GroupUtils.addStudentToGroup(
            studentId = student2.userId,
            groupId = groupId1.toString()
        )

        val lessonId = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString(), groupId2.toString()),
        )

        val lessonId2 = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString()),
        )

        val solutionId = LessonsUtils.createSolution(
            lessonId = lessonId.toString(),
            author = student.userId,
            teacherId = teacher.userId
        )

        val solutionId2 = LessonsUtils.createSolution(
            lessonId = lessonId2.toString(),
            author = student2.userId,
            teacherId = teacher.userId,
            status = SolutionStatus.Reviewed
        )

        val body = client.sendRequest(
            url = "/api/v1/solutions?lessonId=$lessonId&page_size=10&page=1&group_id=$groupId2",
            HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<SolutionListResponse>()

        assertEquals(
            1,
            body.total
        )

        assertEquals(
            1,
            body.items.size
        )
    }

    @Test
    fun test_solution_list_not_all() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val teacher2 = client.registerUniqueTeacher()
        val student = client.registerUniqueStudent()
        val student2 = client.registerUniqueStudent()

        val subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        val subjectId2 = SubjectUtils.createSubjectInDatabase(teacher2.userId).toString()
        val groupId1 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )
        val groupId2 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId2,
            ownerId = teacher2.userId
        )

        GroupUtils.addStudentToGroup(
            studentId = student.userId,
            groupId = groupId1.toString()
        )
        GroupUtils.addStudentToGroup(
            studentId = student2.userId,
            groupId = groupId1.toString()
        )

        val lessonId = LessonsUtils.createLesson(
            author = teacher.userId,
            groups = listOf(groupId1.toString()),
        )

        val lessonId2 = LessonsUtils.createLesson(
            author = teacher2.userId,
            groups = listOf(groupId2.toString()),
        )

        val solutionId = LessonsUtils.createSolution(
            lessonId = lessonId.toString(),
            author = student.userId,
            teacherId = teacher.userId
        )

        val solutionId2 = LessonsUtils.createSolution(
            lessonId = lessonId2.toString(),
            author = student2.userId,
            teacherId = teacher2.userId,
            status = SolutionStatus.Reviewed
        )

        val body = client.sendRequest(
            url = "/api/v1/solutions?lessonId=$lessonId&page_size=10&page=1",
            HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<SolutionListResponse>()

        assertEquals(
            1,
            body.total
        )

        assertEquals(
            1,
            body.items.size
        )
    }
}