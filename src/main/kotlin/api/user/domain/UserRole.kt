package itmo.edugoolda.api.user.domain

enum class UserRole(val data: String) {
    Teacher("teacher"),
    Student("student");

    companion object {
        fun fromString(data: String) = entries.firstOrNull { it.data == data }
        fun UserRole.toDTO() = data
    }
}
