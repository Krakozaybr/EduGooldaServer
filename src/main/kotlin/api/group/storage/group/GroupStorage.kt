package itmo.edugoolda.api.group.storage.group

import itmo.edugoolda.api.group.domain.model.GroupDetailsDomain
import itmo.edugoolda.api.group.domain.model.GroupEntityDomain
import itmo.edugoolda.api.group.domain.model.GroupInfoDomain
import itmo.edugoolda.api.user.domain.UserInfoDomain
import itmo.edugoolda.utils.EntityIdentifier
import itmo.edugoolda.utils.database.Paged

interface GroupStorage {
    suspend fun createGroup(
        name: String,
        description: String?,
        subjectId: EntityIdentifier,
        ownerId: EntityIdentifier
    ): EntityIdentifier

    suspend fun updateGroup(
        groupId: EntityIdentifier,
        name: String,
        description: String?,
        subjectId: EntityIdentifier,
        isActive: Boolean
    )

    suspend fun deleteGroup(groupId: EntityIdentifier)

    suspend fun getGroupInfo(groupId: EntityIdentifier): GroupEntityDomain?

    // Returns whether user was removed
    suspend fun removeUsersFromGroup(id: EntityIdentifier, groupId: EntityIdentifier): Boolean

    suspend fun getGroupStudentsCount(
        groupId: EntityIdentifier
    ): Int

    suspend fun getGroupStudents(
        skip: Int,
        maxCount: Int,
        groupId: EntityIdentifier
    ): Paged<UserInfoDomain>

    suspend fun getGroupBanned(
        skip: Int,
        maxCount: Int,
        groupId: EntityIdentifier
    ): Paged<UserInfoDomain>

    suspend fun getStudentGroups(
        skip: Int,
        maxCount: Int,
        userId: EntityIdentifier,
        query: String?,
        subjectId: EntityIdentifier?
    ): Paged<GroupInfoDomain>

    suspend fun getTeacherGroups(
        skip: Int,
        maxCount: Int,
        userId: EntityIdentifier,
        query: String?,
        subjectId: EntityIdentifier?
    ): Paged<GroupInfoDomain>

    suspend fun getGroupDetails(
        groupId: EntityIdentifier
    ): GroupDetailsDomain

    suspend fun checkStudentIsParticipant(
        groupId: EntityIdentifier,
        userId: EntityIdentifier
    ): Boolean

    suspend fun checkUserIsBanned(
        groupId: EntityIdentifier,
        userId: EntityIdentifier
    ): Boolean
}