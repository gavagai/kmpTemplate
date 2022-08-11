package com.teddyfreddy.kmp

import org.koin.core.context.startKoin

@Suppress("unused") // Invoked from iOS
fun startKoin() {
    startKoin {
        modules(sharedModule)
    }
}