package com.teddyfreddy.kmp.network

import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NetworkSession {
    companion object {
        inline fun <reified T>execute(request: NetworkRequest) : Flow<NetworkResponse<T>> {
            return request
                .asFlow()
                .map { response ->
                    if (response.status.value !in 200..299) {
                        val payload = try {
                            response.body<ErrorPayload>()
                        }
                        catch (e: Exception) {
                            null
                        }
                        if (payload != null) {
                            if (response.status.value == 422) {
                                throw NetworkRequestError.ValidationError(payload)
                            }
                            else if (response.status.value in 500..599) {
                                if (response.status.value == 503) {
                                    throw NetworkRequestError.ServiceUnavailable(response.headers["Retry-After"])
                                }
                                else {
                                    throw NetworkRequestError.ServerError(response.status.value, payload.message)
                                }
                            }
                        }
                        else {
                            if (response.status.value == 401) {
                                throw NetworkRequestError.Unauthenticated
                            }
                            else if (response.status.value == 401) {
                                throw NetworkRequestError.Unauthorized
                            }
                        }

                        throw NetworkRequestError.HttpError(status = response.status.value)
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

        fun authorizationHeader(username: String, password: String) : String {
            val token = basicToken(username, password)
            return  "Basic $token"
        }

        private fun basicToken(username: String, password: String) : String {
            return String(Base64.encoder.encode("$username:$password".toByteArray()))
        }

    }
}
