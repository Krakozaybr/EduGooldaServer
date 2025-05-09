package itmo.edugoolda.api.error.exceptions

class IdFormatException(
    field: String? = null
) : DataFormatException(
    description = when (field) {
        null -> "Query id isn't formatted as UUID"
        else -> "Error in field $field"
    }
)
