package itmo.edugoolda.api.group.storage.subject

import itmo.edugoolda.api.group.domain.model.SubjectDomain
import itmo.edugoolda.utils.EntityIdentifier

interface SubjectStorage {
    suspend fun getSubjects(): List<SubjectDomain>

    suspend fun checkExists(subjectId: EntityIdentifier): Boolean

    suspend fun getById(subjectId: EntityIdentifier): SubjectDomain?
}
