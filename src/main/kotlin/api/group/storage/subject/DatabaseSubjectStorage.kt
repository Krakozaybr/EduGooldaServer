package itmo.edugoolda.api.group.storage.subject

import itmo.edugoolda.api.group.domain.model.SubjectDomain
import itmo.edugoolda.api.group.storage.entities.SubjectEntity
import itmo.edugoolda.api.group.storage.entities.toDomain
import itmo.edugoolda.api.group.storage.tables.SubjectTable
import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.EntityIdentifier
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseSubjectStorage : SubjectStorage {
    override suspend fun getSubjects(ownerId: EntityIdentifier) = transaction {
        SubjectEntity.find {
            SubjectTable.ownerId eq ownerId.value
        }.map { it.toDomain() }
    }

    override suspend fun checkExists(name: String, ownerId: EntityIdentifier): Boolean = transaction {
        SubjectEntity.find {
            listOf(
                SubjectTable.name eq name,
                SubjectTable.ownerId eq ownerId.value
            ).compoundAnd()
        }.empty().not()
    }

    override suspend fun checkExists(subjectId: EntityIdentifier) = transaction {
        SubjectTable.select(SubjectTable.id)
            .where { SubjectTable.id eq subjectId.value }
            .singleOrNull() != null
    }

    override suspend fun getById(subjectId: EntityIdentifier) = transaction {
        SubjectEntity.findById(subjectId.value)?.toDomain()
    }

    override suspend fun createSubject(
        name: String,
        ownerId: EntityIdentifier
    ): SubjectDomain = transaction {
        SubjectEntity.new {
            this.name = name
            this.ownerId = EntityID(ownerId.value, UserTable)
        }.toDomain()
    }

    override suspend fun deleteSubject(id: EntityIdentifier): Unit = transaction {
        SubjectEntity.findById(id.value)?.delete()
    }

    override suspend fun checkIsUserOwner(
        subjectId: EntityIdentifier,
        userId: EntityIdentifier
    ): Boolean = transaction {
        SubjectEntity.findById(subjectId.value)?.ownerId?.value == userId.value
    }
}