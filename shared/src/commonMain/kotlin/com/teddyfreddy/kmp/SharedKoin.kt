package com.teddyfreddy.kmp

import com.arkivanov.mvikotlin.core.store.StoreFactory
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import org.koin.dsl.bind

val sharedModule = module {
    singleOf(::Platform)
    singleOf(::Greeting)
    singleOf(::DefaultStoreFactory) bind StoreFactory::class

    factory {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}
