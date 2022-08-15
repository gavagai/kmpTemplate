package com.teddyfreddy.kmp.network

import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class NetworkSession {
    companion object {
        inline fun <reified T>execute(request: NetworkRequest) : Flow<NetworkResponse<T>> {
            return request
                .asFlow()
                .flowOn(Dispatchers.Main)
                .map { response ->
                    if (response.status.value !in 200..299) {
                        val payload = try {
                            response.body<ErrorPayload>()
                        }
                        catch (e: Exception) {
                            null
                        }
                        when (response.status.value) {
                            401 -> throw NetworkRequestError.Unauthenticated
                            403 -> throw NetworkRequestError.Unauthorized
                            422 -> if (payload != null) throw NetworkRequestError.ValidationError(payload)
                                   else throw NetworkRequestError.HttpError(response.status.value)
                            500 -> throw NetworkRequestError.ServerError(response.status.value, payload?.message ?: "")
                            503 -> throw NetworkRequestError.ServiceUnavailable(response.headers["Retry-After"])
                            else -> throw NetworkRequestError.HttpError(response.status.value)
                        }
                    }

                    response
                }
                .map { response ->
                    if (response.contentLength()!! > 0) {
                        try {
                            NetworkResponse<T>(response.body<T>(), response)
                        }
                        catch (e: Exception) {
                            throw NetworkRequestError.DecodingError
                        }
                    }
                    else {
                        NetworkResponse<T>(null, response)
                    }
                }
        }

        fun basicAuthorizationHeader(username: String, password: String) : String {
            val token = basicToken(username, password)
            return  "Basic $token"
        }

        private fun basicToken(username: String, password: String) : String {
            return String(Base64.encoder.encode("$username:$password".toByteArray()))
        }

    }
}
