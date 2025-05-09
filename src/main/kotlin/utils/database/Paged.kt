package itmo.edugoolda.utils.database

import org.jetbrains.exposed.sql.SizedIterable

data class Paged<T>(
    val entities: List<T>,
    val total: Int
) {
    fun <R> map(transform: (T) -> R) = Paged(
        total = total,
        entities = entities.map(transform)
    )

    companion object {
        fun <T> of(
            skip: Int,
            count: Int,
            iterable: SizedIterable<T>
        ): Paged<T> {
            val total = iterable.count()

            return Paged(
                entities = iterable.offset(skip.toLong()).limit(count).toList(),
                total = total.toInt()
            )
        }
    }
}
