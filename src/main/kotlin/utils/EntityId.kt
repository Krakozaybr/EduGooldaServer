package itmo.edugoolda.utils

import java.util.*

class EntityId private constructor(val value: UUID) {

    val stringValue get() = value.toString()

    companion object {
        fun parse(uuid: UUID) = EntityId(uuid)

        fun parse(value: String): EntityId? = runCatching {
            EntityId(UUID.fromString(value))
        }.getOrNull()
    }
}
