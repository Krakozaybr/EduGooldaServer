package module.subject

import itmo.edugoolda.api.group.storage.tables.SubjectTable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

object SubjectUtils {
    val DefaultSubjectName = "Test subject"

    fun createSubjectInDatabase(
        ownerId: String,
        name: String = DefaultSubjectName
    ) = transaction {
        SubjectTable.insertAndGetId {
            it[SubjectTable.name] = name
            it[SubjectTable.ownerId] = UUID.fromString(ownerId)
        }.value
    }
}