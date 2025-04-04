package itmo.edugoolda.api.auth.domain

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
)
