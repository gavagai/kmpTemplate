package com.teddyfreddy.kmp.network

sealed class NetworkRequestError : Exception() {
    data class InvalidRequest(val error: String) : NetworkRequestError()
    data class TransportError(val error: Exception) : NetworkRequestError()
    object InvalidResponse : NetworkRequestError()
    data class HttpError(val status: Int) : NetworkRequestError()
    object Unauthenticated : NetworkRequestError()
    object Unauthorized : NetworkRequestError()
    data class ServerError(val status: Int, val reason: String) : NetworkRequestError()
    data class ServiceUnavailable(val retry: String?) : NetworkRequestError()
    object DecodingError : NetworkRequestError()
    data class ValidationError(val response: ErrorPayload) : NetworkRequestError()
    data class StandardizedError(val response: ErrorPayload) : NetworkRequestError()
    object Unknown : NetworkRequestError()

    val errorDescription: String?
        get() = when (this) {
            is InvalidRequest -> "Request URL Error"
            is TransportError -> "Communications Error"
            InvalidResponse -> "Invalid Server Response"
            DecodingError -> "Server Response Decoding Error"
            is HttpError -> "HTTP Error"
            Unauthenticated -> "Not Authenticated"
            Unauthorized -> "Not Authorized"
            is ServerError -> "Unexpected Server Error"
            is ServiceUnavailable -> "Service Unavailable"
            is ValidationError -> "Server Validation Error"
            is StandardizedError -> "Server Request Failed"
            Unknown ->  "Unknown Error"
            else -> null
        }

    override val message: String?
        get() = errorDescription

    val failureReason: String?
        get() = when (this) {
            is InvalidRequest -> error
            is TransportError -> error.message
            InvalidResponse -> "Response lacked URLResponse"
            DecodingError -> "Error decoding response"
            is HttpError -> when (status) {
                404 -> "Resource not found"
                else -> "HTTP error status $status"
            }
            Unauthenticated -> "Incorrect credentials"
            Unauthorized -> "Cannot perform this operation"
            is ServerError -> "Server error ($status): $reason"
            is ServiceUnavailable -> "Service temporarily unavailable"
            is ValidationError -> standardizedErrorDescription(response)
            is StandardizedError -> standardizedErrorDescription(response)
            Unknown -> null
            else -> null
        }

    val recoverySuggestion: String?
        get() = when (this) {
            is InvalidRequest -> "Report this as a bug"
            is TransportError -> "Try again later"
            InvalidResponse -> "Report this as a bug"
            DecodingError -> "Report this as a bug"
            is HttpError -> when (this.status) {
                404 -> "Did you enter an incorrect URL somewhere?"
                else -> null
            }
            Unauthenticated -> "Please try again with the correct credentials"
            Unauthorized -> "If you need this level of access contact your Administrator"
            is ServerError -> null
            is ServiceUnavailable -> {
                if (retry != null) {
                    try {
                        val retryInt = retry.toInt()
                        "Try again after $retryInt seconds"
                    }
                    catch (e: NumberFormatException) {
                        "Try again after $retry"
                    }
                } else {
                    null
                }
            }
            is ValidationError -> "The server has rejected the request because some of the data was invalid - see if the details you a hint"
            is StandardizedError -> "See if the details in the error give you a hint"
            Unknown -> "Report this as a bug"
            else -> null
        }



    val description : String
        get() = failureReason ?: ""

    private fun standardizedErrorDescription(response: ErrorPayload) : String {
        var description = "Code: ${response.code}\nMessage: ${response.message}"
        if (response.errors != null) {
            response.errors.forEach { error ->
                description += "\n  Field: ${error.field}\n    Code: ${error.code}\n    Message: ${error.message}"
            }
        }
        return description
    }
}
