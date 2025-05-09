package module.subject

import itmo.edugoolda.api.group.storage.tables.SubjectTable
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

object SubjectUtils {
    val DefaultSubjectName = "Test subject"

    fun createSubjectInDatabase(name: String = DefaultSubjectName) = transaction {
        SubjectTable.insertAndGetId {
            it[SubjectTable.name] = name
        }.value
    }
}