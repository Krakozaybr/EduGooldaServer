package itmo.edugoolda.utils

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
