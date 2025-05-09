package itmo.edugoolda.api.group.storage.subject

import itmo.edugoolda.api.group.storage.entities.SubjectEntity
import itmo.edugoolda.api.group.storage.entities.toDomain
import itmo.edugoolda.api.group.storage.tables.SubjectTable
import itmo.edugoolda.utils.EntityIdentifier
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseSubjectStorage : SubjectStorage {
    override suspend fun getSubjects() = transaction {
        SubjectEntity.all().map { it.toDomain() }
    }

    override suspend fun checkExists(subjectId: EntityIdentifier) = transaction {
        SubjectTable.select(SubjectTable.id)
            .where { SubjectTable.id eq subjectId.value }
            .singleOrNull() != null
    }

    override suspend fun getById(subjectId: EntityIdentifier) = transaction {
        SubjectEntity.findById(subjectId.value)?.toDomain()
    }
}