package module.requests_and_invitations

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.group.domain.model.JoinRequestStatus
import itmo.edugoolda.api.group.exception.GroupRequestAlreadyExistsException
import itmo.edugoolda.api.group.exception.UserIsBannedException
import itmo.edugoolda.api.group.route.v1.requests_and_invitations.JoinGroupByCodeRequest
import itmo.edugoolda.api.group.route.v1.requests_and_invitations.JoinGroupResponse
import itmo.edugoolda.api.group.route.v1.requests_and_invitations.JoiningInformationResponse
import itmo.edugoolda.api.group.storage.tables.JoinRequestTable
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import module.*
import module.group.GroupUtils
import module.subject.SubjectUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class JoinGroupByCodeTests : ModuleTest {

    @Test
    fun test_join_by_code_correct() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val codeBody = client.sendRequest(
            url = "/api/v1/group_invitation/$groupId",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<JoiningInformationResponse>()

        val body = client.sendRequest(
            url = "/api/v1/group/join",
            method = HttpMethod.Post,
            accessToken = student.accessToken,
            body = JoinGroupByCodeRequest(
                code = codeBody.code
            )
        ).expectOk().body<JoinGroupResponse>()

        assertEquals(
            expected = groupId.toString(),
            actual = body.groupId
        )

        assertEquals(
            expected = 1,
            actual = transaction {
                JoinRequestTable.selectAll()
                    .where { JoinRequestTable.userId eq UUID.fromString(student.userId) }
                    .count().toInt()
            }
        )
    }

    @Test
    fun test_join_by_code_banned() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val codeBody = client.sendRequest(
            url = "/api/v1/group_invitation/$groupId",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<JoiningInformationResponse>()

        JoiningUtils.banStudent(groupId.toString(), student.userId)

        client.sendRequest(
            url = "/api/v1/group/join",
            method = HttpMethod.Post,
            accessToken = student.accessToken,
            body = JoinGroupByCodeRequest(
                code = codeBody.code
            )
        ).expectError(HttpStatusCode.Forbidden, UserIsBannedException.CODE)

        assertEquals(
            expected = 0,
            actual = transaction {
                JoinRequestTable.selectAll()
                    .where { JoinRequestTable.userId eq UUID.fromString(student.userId) }
                    .count().toInt()
            }
        )
    }

    @Test
    fun test_join_by_code_already_sent() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val codeBody = client.sendRequest(
            url = "/api/v1/group_invitation/$groupId",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<JoiningInformationResponse>()

        client.sendRequest(
            url = "/api/v1/group/join",
            method = HttpMethod.Post,
            accessToken = student.accessToken,
            body = JoinGroupByCodeRequest(
                code = codeBody.code
            )
        ).expectOk()

        client.sendRequest(
            url = "/api/v1/group/join",
            method = HttpMethod.Post,
            accessToken = student.accessToken,
            body = JoinGroupByCodeRequest(
                code = codeBody.code
            )
        ).expectError(HttpStatusCode.BadRequest, GroupRequestAlreadyExistsException.CODE)

        assertEquals(
            expected = 1,
            actual = transaction {
                JoinRequestTable.selectAll()
                    .where { JoinRequestTable.userId eq UUID.fromString(student.userId) }
                    .count().toInt()
            }
        )
    }

    @Test
    fun test_join_by_code_correct_with_one_declined() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val student = client.registerStudent()

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val codeBody = client.sendRequest(
            url = "/api/v1/group_invitation/$groupId",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<JoiningInformationResponse>()

        client.sendRequest(
            url = "/api/v1/group/join",
            method = HttpMethod.Post,
            accessToken = student.accessToken,
            body = JoinGroupByCodeRequest(
                code = codeBody.code
            )
        ).expectOk()

        JoiningUtils.setStatusOfJoiningRequest(
            groupId = groupId.toString(),
            studentId = student.userId,
            status = JoinRequestStatus.Declined
        )

        client.sendRequest(
            url = "/api/v1/group/join",
            method = HttpMethod.Post,
            accessToken = student.accessToken,
            body = JoinGroupByCodeRequest(
                code = codeBody.code
            )
        ).expectOk()

        assertEquals(
            expected = 2,
            actual = transaction {
                JoinRequestTable.selectAll()
                    .where { JoinRequestTable.userId eq UUID.fromString(student.userId) }
                    .count().toInt()
            }
        )
    }

    @Test
    fun test_join_by_code_wrong_role() = testJsonRequests { client ->
        val teacher = client.registerTeacher()
        val teacher2 = client.registerStudent(
            DefaultRegisterTeacherRequest.copy(
                email = "another@email.com"
            )
        )

        val groupId = GroupUtils.createGroupInDatabase(
            ownerId = teacher.userId,
            subjectId = SubjectUtils.createSubjectInDatabase(teacher.userId).toString()
        )

        val codeBody = client.sendRequest(
            url = "/api/v1/group_invitation/$groupId",
            method = HttpMethod.Get,
            accessToken = teacher.accessToken
        ).expectOk().body<JoiningInformationResponse>()

        client.sendRequest(
            url = "/api/v1/group/join",
            method = HttpMethod.Post,
            accessToken = teacher2.accessToken,
            body = JoinGroupByCodeRequest(
                code = codeBody.code
            )
        ).expectError(HttpStatusCode.Forbidden, UnsuitableUserRoleException.CODE)

        assertEquals(
            expected = 0,
            actual = transaction {
                JoinRequestTable.selectAll()
                    .where { JoinRequestTable.userId eq UUID.fromString(teacher2.userId) }
                    .count().toInt()
            }
        )
    }
}