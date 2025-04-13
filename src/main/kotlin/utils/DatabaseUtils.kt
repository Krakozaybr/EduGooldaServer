package itmo.edugoolda.utils

import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and

fun Iterable<Op<Boolean>>.reduceByAnd() = reduce { c1, c2 -> c1 and c2 }
