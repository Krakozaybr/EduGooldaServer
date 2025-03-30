package itmo.edugoolda.api.auth.domain

interface AuthCredentials {

    val providerType: AuthProviderType

    data class EmailPassword(val email: String, val password: String) : AuthCredentials {
        override val providerType = AuthProviderType.Email
    }
}