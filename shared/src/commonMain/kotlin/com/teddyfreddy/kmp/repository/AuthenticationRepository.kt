package com.teddyfreddy.kmp.repository

import com.teddyfreddy.common.network.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

class AuthenticationRepository {
    companion object {
        fun authenticate(
            scope: CoroutineScope,
            username: String,
            password: String,
            emailVerificationCode: String? = null,
            completion: (NetworkResponse<LoginResponseDTO>?, Throwable?) -> Unit
        ) {
            scope.launch {
                try {
                    NetworkSession.execute<LoginResponseDTO>(
                        loginRequest(username, password, emailVerificationCode)
                    )
                        .flowOn(Dispatchers.Main)
                        .catch { e ->
                            completion(null, e)
                        }
                        .collect {
                            NetworkSession.basicAuthorizationToken = NetworkSession.BasicAuthorizationToken(username, password)
                            completion(it, null)
                        }
                }
                catch (e: NetworkRequestError.TransportError) {
                    completion(null, e)
                }
            }
        }

        private fun loginRequest(
            username: String,
            password: String,
            emailVerificationCode: String? = null
        ) : NetworkRequest {
            val authToken = NetworkSession.BasicAuthorizationToken(username, password)
            return NetworkRequest("http://10.0.1.173:8080/login") {
                method = HttpMethod.Post
                header(
                    "Authorization",
                    "Basic ${authToken.token}"
                )
                header("Content-Type", "application/json")
                setBody(
                    LoginCredentialsDTO(
                        username = username,
                        password = password,
                        emailVerificationCode = emailVerificationCode
                    )
                )
            }
        }

        fun sendVerificationCode(
            scope: CoroutineScope,
            username: String,
            password: String,
            completion: (NetworkResponse<Unit>?, Throwable?) -> Unit
        ) {
            scope.launch {
                try {
                    NetworkSession.execute<Unit>(
                        sendVerificationCodeRequest(username, password)
                    )
                        .flowOn(Dispatchers.Main)
                        .catch { e ->
                            completion(null, e)
                        }
                        .collect {
                            completion(it, null)
                        }
                }
                catch (e: NetworkRequestError.TransportError) {
                    completion(null, e)
                }
            }
        }

        private fun sendVerificationCodeRequest(
            username: String,
            password: String
        ) : NetworkRequest {
            val authToken = NetworkSession.BasicAuthorizationToken(username, password)
            return NetworkRequest("http://10.0.1.173:8080/code/email?username=$username") {
                method = HttpMethod.Post
                header(
                    "Authorization",
                    "Basic ${authToken.token}"
                )
            }
        }
    }
}



@Serializable
data class LoginCredentialsDTO (
    val username : String,
    val password : String,
    val emailVerificationCode : String?
)

@Serializable
data class LoginResponseDTO (
    val member: MemberDetailsDTO,
    val organization: OrganizationDTO
)

@Serializable
data class OrganizationDTO (
    val id: Int? = null,
    val name: String? = null,
    val abbreviation: String? = null,
    val primaryContact: Int? = null,
    val allowMembershipRequests: Boolean? = false
)

@Serializable
data class MemberDetailsDTO (
    val member: Int,
    val administrator: Boolean? = false,

    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    @Serializable(with = LocalDateSerializer::class) val dateOfBirth: LocalDate? = null,
    val username: String
)
