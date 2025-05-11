package itmo.edugoolda.api.user.domain

enum class UserRole(val data: String) {
    Teacher("teacher"),
    Student("student");

    companion object {
        fun fromString(data: String) = entries.firstOrNull { it.data == data.lowercase() }

        fun safeFromString(data: String) = entries.first { it.data == data.lowercase() }

        fun UserRole.toDTO() = data
    }
}
