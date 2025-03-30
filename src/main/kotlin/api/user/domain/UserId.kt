package itmo.edugoolda.api.user.domain

import java.util.*

class UserId private constructor(val value: UUID) {

    val stringValue get() = value.toString()

    companion object {
        fun parse(uuid: UUID) = UserId(uuid)

        fun parse(value: String): UserId? = runCatching {
            UserId(UUID.fromString(value))
        }.getOrNull()
    }
}
