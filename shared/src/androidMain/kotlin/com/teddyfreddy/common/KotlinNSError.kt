package com.teddyfreddy.common

actual class KotlinNSError actual constructor(actual val throwable: Throwable)  {
    actual val description: String
        get() = throwable.message ?: ""
    actual val failureReason: String?
        get() = description
    actual val recoverySuggestion: String?
        get() = null
}