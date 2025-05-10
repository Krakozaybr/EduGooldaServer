package itmo.edugoolda.utils

import itmo.edugoolda.api.group.utils.maxLength
import itmo.edugoolda.api.user.storage.tables.UserTable

private const val EMAIL_REGEX = "[a-z0-9.!#$%&'`*+\\-/=^_{}|~]+@((\\.)?[a-zA-Z0-9\\-])+$"

fun validateEmail(email: String): Boolean {
    return email.matches(Regex(EMAIL_REGEX))
}

fun validatePassword(password: String): Boolean {
    if (password.length !in 6..32) return false

    if (!password.any { it.isDigit() }) return false

    if (!password.any { it.isLetter() && it == it.uppercaseChar() }) return false

    if (!password.any { it.isLetter() && it == it.lowercaseChar() }) return false

    return true
}

fun validateBio(bio: String?) = bio == null || bio.length < 5000

fun validateName(name: String): Boolean {
    if (name.isBlank()) return false

    if (name.length > UserTable.name.maxLength) return false

    return true
}
