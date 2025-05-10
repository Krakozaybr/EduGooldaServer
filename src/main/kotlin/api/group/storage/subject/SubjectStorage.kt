package itmo.edugoolda.api.group.storage.subject

import itmo.edugoolda.api.group.domain.model.SubjectDomain
import itmo.edugoolda.utils.EntityIdentifier

interface SubjectStorage {
    suspend fun getSubjects(ownerId: EntityIdentifier): List<SubjectDomain>

    suspend fun checkIsUserOwner(subjectId: EntityIdentifier, userId: EntityIdentifier): Boolean

    suspend fun checkExists(subjectId: EntityIdentifier): Boolean

    suspend fun checkExists(name: String, ownerId: EntityIdentifier): Boolean

    suspend fun getById(subjectId: EntityIdentifier): SubjectDomain?

    suspend fun createSubject(name: String, ownerId: EntityIdentifier): SubjectDomain

    suspend fun deleteSubject(id: EntityIdentifier)
}
