package itmo.edugoolda.api.auth.storage.refresh_tokens

import itmo.edugoolda.api.user.storage.UserTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object RefreshTokensTable : UUIDTable(
    name = "refresh_tokens_table",
    columnName = "id"
) {
    val userId = reference(
        name = "user_id",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val token = varchar("token", 300).index()
    val expiresAt = datetime("expires_at")
}