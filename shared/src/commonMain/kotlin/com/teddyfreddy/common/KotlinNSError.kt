package com.teddyfreddy.common

expect class KotlinNSError(throwable: Throwable) {
    val description: String
    val failureReason: String?
    val recoverySuggestion: String?
    val throwable: Throwable
}