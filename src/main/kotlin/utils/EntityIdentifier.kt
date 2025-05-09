package itmo.edugoolda.utils

import io.ktor.server.routing.*
import itmo.edugoolda.api.error.exceptions.IdFormatException
import java.util.*

class EntityIdentifier private constructor(val value: UUID) {

    val stringValue get() = value.toString()

    companion object {
        fun parse(uuid: UUID) = EntityIdentifier(uuid)

        fun parse(value: String): EntityIdentifier? = runCatching {
            EntityIdentifier(UUID.fromString(value))
        }.getOrNull()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EntityIdentifier

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

fun RoutingContext.idParameter(name: String): EntityIdentifier {
    return call.pathParameters[name]?.takeIf {
        it.isNotBlank()
    }?.let(EntityIdentifier::parse) ?: throw IdFormatException(name)
}
