package domain

import kotlinx.serialization.Serializable

@Serializable
value class EID(private val uuid: String) {
    override fun toString(): String = uuid
}