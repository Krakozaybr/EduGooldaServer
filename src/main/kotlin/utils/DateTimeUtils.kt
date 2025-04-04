package itmo.edugoolda.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toCurrentLocalDateTime() = toLocalDateTime(TimeZone.currentSystemDefault())
