package itmo.edugoolda.api.group.domain.model

import itmo.edugoolda.api.error.exceptions.PrintableEnum

enum class RemoveStudentAction(
    override val string: String
) : PrintableEnum {
    Kick("kick"),
    KickAndBan("kick_and_ban");
}