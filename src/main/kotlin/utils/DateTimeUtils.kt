package itmo.edugoolda.utils

import kotlinx.datetime.*
import java.time.format.DateTimeFormatter

fun Instant.toCurrentLocalDateTime() = toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDateTime.defaultFormat() = toJavaLocalDateTime().format(
    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
)
