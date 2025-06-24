package itmo.edugoolda.api.group.storage.group_request

import itmo.edugoolda.api.group.domain.model.JoinRequestDomain
import itmo.edugoolda.api.group.domain.model.JoinRequestInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.Paged

interface GroupRequestStorage {
    suspend fun sendAddToGroupRequest(groupId: EntityIdentifier, userId: EntityIdentifier)

    suspend fun acceptAddToGroupRequest(requestId: EntityIdentifier)

    suspend fun cancelAddToGroupRequest(requestId: EntityIdentifier)

    suspend fun declineAddToGroupRequest(requestId: EntityIdentifier)

    suspend fun generateCodeForGroup(groupId: EntityIdentifier): String

    suspend fun getGroupIdByCode(code: String): EntityIdentifier?

    suspend fun checkPendingJoinRequestExists(userId: EntityIdentifier, groupId: EntityIdentifier): Boolean

    suspend fun getJoinRequestInfoById(identifier: EntityIdentifier): JoinRequestInfoDomain?

    suspend fun getGroupJoinRequests(
        groupId: EntityIdentifier,
        skip: Int,
        limit: Int
    ): Paged<JoinRequestDomain>

    suspend fun getTeacherJoinRequests(
        userId: EntityIdentifier,
        skip: Int,
        limit: Int
    ): Paged<JoinRequestDomain>

    suspend fun getStudentJoinRequests(
        userId: EntityIdentifier,
        skip: Int,
        limit: Int
    ): Paged<JoinRequestDomain>
}