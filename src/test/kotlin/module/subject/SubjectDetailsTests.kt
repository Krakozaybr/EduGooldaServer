package module.subject

import io.ktor.client.call.*
import io.ktor.http.*
import itmo.edugoolda.api.error.ErrorResponse
import itmo.edugoolda.api.group.dto.SubjectDto
import itmo.edugoolda.api.group.exception.SubjectNotFoundException
import itmo.edugoolda.api.group.storage.entities.SubjectEntity
import module.ModuleTest
import module.registerStudent
import module.sendRequest
import module.testJsonRequests
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SubjectDetailsTests : ModuleTest {
    @Test
    fun testUnknownSubject() = testJsonRequests { client ->
        val tokens = client.registerStudent()

        val resp = client.sendRequest(
            url = "/api/v1/subjects/${UUID.randomUUID()}",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        )
        val body = resp.body<ErrorResponse>()

        assertEquals(
            HttpStatusCode.NotFound,
            resp.status
        )

        assertEquals(
            SubjectNotFoundException.CODE,
            body.errorCode
        )
    }

    @Test
    fun testSubjectDetails() = testJsonRequests { client ->
        val tokens = client.registerStudent()

        val expectedName = "Test subject"

        val subjectId = transaction {
            SubjectEntity.new {
                name = expectedName
            }.id.value
        }

        val expectedSubject = SubjectDto(
            name = expectedName,
            id = subjectId.toString()
        )

        val resp = client.sendRequest(
            url = "/api/v1/subjects/${subjectId}",
            method = HttpMethod.Get,
            accessToken = tokens.accessToken
        )
        val body = resp.body<SubjectDto>()

        assertEquals(
            HttpStatusCode.OK,
            resp.status
        )

        assertEquals(
            expectedSubject,
            body
        )
    }
}