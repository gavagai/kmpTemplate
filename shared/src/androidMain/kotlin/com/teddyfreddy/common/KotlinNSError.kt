package com.teddyfreddy.common

actual class KotlinNSError actual constructor(actual val throwable: Throwable)  {
    actual val description: String
        get() = throwable.message ?: ""
    actual val failureReason: String?
        get() = null
    actual val recoverySuggestion: String?
        get() = null
}