package itmo.edugoolda.api.user.exceptions

import itmo.edugoolda.utils.EntityId

class UserAlreadyDeletedException(val userId: EntityId) : Exception()
