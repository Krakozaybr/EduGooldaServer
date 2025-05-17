package module.lessons

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.exception.MustBeGroupOwnerException
import itmo.edugoolda.api.lessons.dto.LessonFullDetailsDTO
import itmo.edugoolda.api.lessons.exception.*
import itmo.edugoolda.api.lessons.route.v1.CreateLessonRequest
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import kotlinx.datetime.Clock
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days

class CreateLessonTests : ModuleTest {

    @Test
    fun test_create_lesson_correct() = testJsonRequests { client ->
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

        val body = client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "lesson",
                description = null,
                groupIds = listOf(groupId1.toString(), groupId2.toString()),
                isEstimatable = false,
            )
        ).expectOk().body<LessonFullDetailsDTO>()

        assertEquals(
            setOf(groupId1.toString(), groupId2.toString()),
            body.groups.map { it.id }.toSet()
        )

        assertEquals(
            teacher.userId,
            body.teacher.id
        )
    }

    @Test
    fun test_create_lesson_not_group_owner() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()
        val teacher2 = client.registerUniqueTeacher()

        val subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        val groupId1 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher2.userId
        )
        val groupId2 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = teacher.userId
        )

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "lesson",
                description = null,
                groupIds = listOf(groupId1.toString(), groupId2.toString()),
                isEstimatable = false,
            )
        ).expectError(HttpStatusCode.Forbidden, MustBeGroupOwnerException.CODE)
    }

    @Test
    fun test_create_lesson_group_not_found() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "lesson",
                description = null,
                groupIds = listOf(UUID.randomUUID().toString()),
                isEstimatable = false,
            )
        ).expectError(HttpStatusCode.BadRequest, GroupsErrorException.CODE)
    }

    @Test
    fun test_create_lesson_deadline_in_past() = testJsonRequests { client ->
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

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "lesson",
                description = null,
                groupIds = listOf(groupId1.toString(), groupId2.toString()),
                isEstimatable = false,
                deadline = Clock.System.now().epochSeconds
            )
        ).expectError(HttpStatusCode.BadRequest, DeadlineInPastException.CODE)
    }

    @Test
    fun test_create_lesson_deadline_before_open() = testJsonRequests { client ->
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

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "lesson",
                description = null,
                groupIds = listOf(groupId1.toString(), groupId2.toString()),
                isEstimatable = false,
                deadline = Clock.System.now().plus(1.days).epochSeconds,
                opensAt = Clock.System.now().plus(2.days).epochSeconds,
            )
        ).expectError(HttpStatusCode.BadRequest, OpensAfterDeadlineException.CODE)
    }

    @Test
    fun test_create_lesson_group_list_is_empty() = testJsonRequests { client ->
        val teacher = client.registerUniqueTeacher()

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "lesson",
                description = null,
                groupIds = listOf(),
                isEstimatable = false,
                deadline = Clock.System.now().plus(1.days).epochSeconds,
                opensAt = Clock.System.now().plus(2.days).epochSeconds,
            )
        ).expectError(HttpStatusCode.BadRequest, GroupListIsEmptyException.CODE)
    }

    @Test
    fun test_create_lesson_name_is_empty() = testJsonRequests { client ->
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

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "",
                description = null,
                groupIds = listOf(groupId1.toString(), groupId2.toString()),
                isEstimatable = false,
                deadline = Clock.System.now().plus(1.days).epochSeconds,
                opensAt = Clock.System.now().plus(2.days).epochSeconds,
            )
        ).expectError(HttpStatusCode.BadRequest, LessonNameException.CODE)
    }

    @Test
    fun test_create_lesson_name_too_long() = testJsonRequests { client ->
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

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "sdf".repeat(1323),
                description = null,
                groupIds = listOf(groupId1.toString(), groupId2.toString()),
                isEstimatable = false,
                deadline = Clock.System.now().plus(1.days).epochSeconds,
                opensAt = Clock.System.now().plus(2.days).epochSeconds,
            )
        ).expectError(HttpStatusCode.BadRequest, LessonNameException.CODE)
    }

    @Test
    fun test_create_lesson_description_is_empty() = testJsonRequests { client ->
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

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "sdfsd",
                description = "",
                groupIds = listOf(groupId1.toString(), groupId2.toString()),
                isEstimatable = false,
                deadline = Clock.System.now().plus(1.days).epochSeconds,
                opensAt = Clock.System.now().plus(2.days).epochSeconds,
            )
        ).expectError(HttpStatusCode.BadRequest, LessonDescriptionException.CODE)
    }

    @Test
    fun test_create_lesson_description_is_too_long() = testJsonRequests { client ->
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

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = teacher.accessToken,
            body = CreateLessonRequest(
                name = "sdfsd",
                description = "sdfsd".repeat(12333),
                groupIds = listOf(groupId1.toString(), groupId2.toString()),
                isEstimatable = false,
                deadline = Clock.System.now().plus(1.days).epochSeconds,
                opensAt = Clock.System.now().plus(2.days).epochSeconds,
            )
        ).expectError(HttpStatusCode.BadRequest, LessonDescriptionException.CODE)
    }

    @Test
    fun test_create_not_teacher() = testJsonRequests { client ->
        val student = client.registerUniqueStudent()

        val subjectId = SubjectUtils.createSubjectInDatabase(student.userId).toString()
        val groupId1 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = student.userId
        )
        val groupId2 = GroupUtils.createGroupInDatabase(
            subjectId = subjectId,
            ownerId = student.userId
        )

        client.sendRequest(
            "/api/v1/lesson",
            HttpMethod.Post,
            accessToken = student.accessToken,
            body = CreateLessonRequest(
                name = "sdfsd",
                description = "sdfsd".repeat(12333),
                groupIds = listOf(groupId1.toString(), groupId2.toString()),
                isEstimatable = false,
                deadline = Clock.System.now().plus(1.days).epochSeconds,
                opensAt = Clock.System.now().plus(2.days).epochSeconds,
            )
        ).expectError(HttpStatusCode.Forbidden, UnsuitableUserRoleException.CODE)
    }
}