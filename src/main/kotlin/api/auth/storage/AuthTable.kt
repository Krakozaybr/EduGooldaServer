package itmo.edugoolda.api.auth.storage

import itmo.edugoolda.api.auth.domain.AuthProviderType
import org.jetbrains.exposed.dao.id.UUIDTable

object AuthTable : UUIDTable(
    name = "auth",
    columnName = "id"
) {
    val userId = varchar("user_id", 100)
    val providerType = varchar("provider_type", 30)
    val providerUserId = varchar("provider_user_id", 255)
    val passwordHash = varchar("password_hash", 70)
}

fun AuthProviderType.toDTO() = when (this) {
    AuthProviderType.Email -> "email"
}
