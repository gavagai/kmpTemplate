package com.teddyfreddy.common

import platform.Foundation.NSError

actual class KotlinNSError actual constructor(actual val throwable: Throwable)  {
    actual val description: String
        get() = if (throwable is NSError) throwable.localizedDescription else throwable.message ?: ""
    actual val failureReason: String?
        get() = if (throwable is NSError) throwable.localizedFailureReason else null
    actual val recoverySuggestion: String?
        get() = if (throwable is NSError) throwable.localizedRecoverySuggestion else null
}