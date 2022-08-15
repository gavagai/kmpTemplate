package com.teddyfreddy.kmp.network

import kotlinx.serialization.Serializable

@Serializable
class FieldError(
    val field: String,
    val code: Int,
    val message: String
)

@Serializable
data class ErrorPayload(
    val code: Int,
    val message: String,
    val errors: Array<FieldError>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        other as ErrorPayload

        if (code != other.code) return false
        if (message != other.message) return false
        if (errors != null) {
            if (other.errors == null) return false
            if (!errors.contentEquals(other.errors)) return false
        } else if (other.errors != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code
        result = 31 * result + message.hashCode()
        result = 31 * result + (errors?.contentHashCode() ?: 0)
        return result
    }
}

