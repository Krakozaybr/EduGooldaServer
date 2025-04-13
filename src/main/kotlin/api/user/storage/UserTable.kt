package itmo.edugoolda.api.user.storage

import org.jetbrains.exposed.dao.id.UUIDTable

object UserTable : UUIDTable(
    name = "users",
    columnName = "id"
) {
    val email = varchar("email", 300).index()
    val name = varchar("name", 300)
    val role = varchar("role", 100).index()
    val isDeleted = bool("is_deleted").default(false)
}