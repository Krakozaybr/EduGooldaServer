package itmo.edugoolda.api.lessons.domain

import itmo.edugoolda.api.error.exceptions.PrintableEnum

enum class SolutionStatus(
    override val string: String
) : PrintableEnum {
    Pending("pending"),
    Reviewed("reviewed"),
}