package itmo.edugoolda.api.group.storage.tables

import itmo.edugoolda.api.user.storage.tables.UserTable
import itmo.edugoolda.utils.database.BaseTable
import org.jetbrains.exposed.sql.ReferenceOption

object UserFavouriteGroupTable : BaseTable("user_favourite_table") {
    val userId = reference(
        name = "user_id",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val groupId = reference(
        name = "group_id",
        refColumn = GroupTable.id,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val isFavourite = bool("is_favourite").default(false)

    init {
        uniqueIndex(userId, groupId)
    }
}