package itmo.edugoolda.api.group.domain.model

import itmo.edugoolda.api.error.exceptions.PrintableEnum

enum class JoinRequestAction(override val string: String) : PrintableEnum {
    Cancel("cancel"),
    Accept("accept"),
    Decline("decline"),
    DeclineAndBan("decline_and_ban");
}