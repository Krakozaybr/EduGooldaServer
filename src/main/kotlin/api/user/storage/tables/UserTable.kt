package itmo.edugoolda.api.user.storage.tables

import itmo.edugoolda.api.user.domain.UserRole
import itmo.edugoolda.utils.database.BaseTable

object UserTable : BaseTable(
    name = "users",
    columnName = "id"
) {
    val email = varchar("email", 300).uniqueIndex()
    val name = varchar("name", 300)
    val role = enumeration<UserRole>("role")
    val bio = text("bio").nullable().default(null)
    val isDeleted = bool("is_deleted").default(false)
}