package itmo.edugoolda.api.group.utils

import itmo.edugoolda.api.group.storage.tables.GroupTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.VarCharColumnType

val Column<String>.maxLengthNullable get() = (columnType as VarCharColumnType).colLength
val Column<String?>.maxLength get() = (columnType as VarCharColumnType).colLength

fun checkGroupName(name: String): Boolean {
    return name.isNotBlank()
            && name.length in 1..GroupTable.name.maxLengthNullable
}

fun checkGroupDescription(description: String): Boolean {
    return description.isNotBlank()
            && description.length in 0..GroupTable.name.maxLengthNullable
}
