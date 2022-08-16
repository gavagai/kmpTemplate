package com.teddyfreddy.ios

import com.teddyfreddy.kmp.sharedModule
import org.koin.core.context.startKoin

@Suppress("unused") // Invoked from iOS
fun startKoin() {
    startKoin {
        modules(sharedModule)
    }
}