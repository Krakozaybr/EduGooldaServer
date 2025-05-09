package itmo.edugoolda.api.group.storage.tables

import itmo.edugoolda.utils.database.BaseTable

object SubjectTable : BaseTable("subjects") {
    val name = varchar("name", 300).index()
}