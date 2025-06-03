package domain

import kotlinx.serialization.Serializable

@Serializable
data class AuthResult(val user: User?)
