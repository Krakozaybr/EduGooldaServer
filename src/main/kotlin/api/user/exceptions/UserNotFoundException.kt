package itmo.edugoolda.api.user.exceptions

import itmo.edugoolda.api.user.domain.UserId

class UserNotFoundException(val userId: UserId) : Exception()
