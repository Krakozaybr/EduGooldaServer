package itmo.edugoolda.api.user.exceptions

import itmo.edugoolda.utils.EntityId

class UserNotFoundException(val userId: EntityId) : Exception()
