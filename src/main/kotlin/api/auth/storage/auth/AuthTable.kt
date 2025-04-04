package itmo.edugoolda.api.auth.storage.auth

import itmo.edugoolda.api.auth.domain.AuthProviderType
import itmo.edugoolda.api.user.storage.UserTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object AuthTable : UUIDTable(
    name = "auth",
    columnName = "id"
) {
    val userId = reference(
        name = "user_id",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val providerType = varchar("provider_type", 30)
    val providerUserId = varchar("provider_user_id", 255)
    val passwordHash = varchar("password_hash", 70)
}

fun AuthProviderType.toDTO() = when (this) {
    AuthProviderType.Email -> "email"
}
