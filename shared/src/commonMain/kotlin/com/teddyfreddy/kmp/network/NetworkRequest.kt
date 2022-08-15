package com.teddyfreddy.kmp.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class NetworkRequest(
    val urlString: String,
    val builder: HttpRequestBuilder.() -> Unit
) : KoinComponent {
    private suspend fun request() : HttpResponse {
        val client: HttpClient by inject()
        return client.request(urlString, builder)
    }

    fun asFlow(): Flow<HttpResponse> = flow {
        emit(request())
    }
}

data class NetworkResponse<T>(
    val body: T?,
    val response: HttpResponse
)