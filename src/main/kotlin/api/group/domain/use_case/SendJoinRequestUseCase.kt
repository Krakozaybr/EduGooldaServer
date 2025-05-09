package itmo.edugoolda.api.group.domain.use_case

import itmo.edugoolda.api.group.exception.GroupRequestAlreadyExistsException
import itmo.edugoolda.api.group.exception.UserIsAlreadyParticipantException
import itmo.edugoolda.api.group.exception.UserIsBannedException
import itmo.edugoolda.api.group.storage.group.GroupStorage
import itmo.edugoolda.api.group.storage.group_request.GroupRequestStorage
import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.api.user.exceptions.UnsuitableUserRoleException
import itmo.edugoolda.api.user.exceptions.UserNotFoundException
import itmo.edugoolda.api.user.storage.UserStorage
import itmo.edugoolda.utils.EntityIdentifier

class SendJoinRequestUseCase(
    private val groupStorage: GroupStorage,
    private val groupRequestStorage: GroupRequestStorage,
    private val userStorage: UserStorage
) {
    suspend operator fun invoke(
        userId: EntityIdentifier,
        groupId: EntityIdentifier
    ) {
        val user = userStorage.getUserById(userId)
            ?: throw UserNotFoundException(userId)

        if (user.role != UserRole.Student) {
            throw UnsuitableUserRoleException(UserRole.Student)
        }

        if (groupRequestStorage.checkPendingJoinRequestExists(userId, groupId)) {
            throw GroupRequestAlreadyExistsException()
        }

        if (groupStorage.checkUserIsBanned(groupId, userId)) {
            throw UserIsBannedException(
                userId = userId,
                groupId = groupId
            )
        }

        if (groupStorage.checkStudentIsParticipant(groupId, userId)) {
            throw UserIsAlreadyParticipantException(
                userId = userId,
                groupId = groupId
            )
        }

        groupRequestStorage.sendAddToGroupRequest(
            userId = userId,
            groupId = groupId
        )
    }
}