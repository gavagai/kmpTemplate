package com.teddyfreddy.kmp.repository

import com.teddyfreddy.kmp.login.LoginStore
import com.teddyfreddy.kmp.network.NetworkRequest
import com.teddyfreddy.kmp.network.NetworkRequestError
import com.teddyfreddy.kmp.network.NetworkResponse
import com.teddyfreddy.kmp.network.NetworkSession
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class AuthenticationRepository {
    companion object {
        fun authenticate(
            scope: CoroutineScope,
            username: String,
            password: String,
            completion: (NetworkResponse<LoginDTO>?, String?) -> Unit) {
            val authToken = NetworkSession.BasicAuthorizationToken(username, password)
            scope.launch {
                try {
                    NetworkSession.execute<LoginDTO>(
                        loginRequest(authToken)
                    )
                        .flowOn(Dispatchers.Main)
                        .catch { e ->
                            completion(null, e.message)
                        }
                        .collect {
                            NetworkSession.basicAuthorizationToken = authToken
                            completion(it, null)
                        }
                }
                catch (e: NetworkRequestError.TransportError) {
                    completion(null, e.message)
                }
            }
        }

        private fun loginRequest(authToken: NetworkSession.BasicAuthorizationToken) : NetworkRequest =
            NetworkRequest("http://10.0.1.173:8080/login") {
                method = HttpMethod.Post
                header(
                    "Authorization",
                    "Basic ${authToken.token}"
                )
                header("Content-Type", "application/json")
                setBody(
                    LoginCredentialsDTO(
                        username = authToken.username,
                        password = authToken.password
                    )
                )
            }
    }
}



@Serializable
data class LoginCredentialsDTO (
    val username : String,
    val password : String
)

@Serializable
data class LoginDTO (
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



//         Instant.parse(value).toLocalDateTime(TimeZone.UTC)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val string = "${value.year}-${value.month}-${value.dayOfMonth} ${value.hour}:${value.minute}:${value.second}"
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString() // yyyy-MM-ddThh:mm:ss
        return LocalDateTime(
            string.substring(0, 4).toInt(),
            string.substring(5, 7).toInt(),
            string.substring(8, 10).toInt(),
            string.substring(11, 13).toInt(),
            string.substring(14, 16).toInt(),
            string.substring(17, 19).toInt()
        )
    }
}

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        val string = "${value.year}-${value.month}-${value.dayOfMonth}"
        encoder.encodeString(string)
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        val string = decoder.decodeString() // yyyy-MM-dd
        return LocalDate(
            string.substring(0, 4).toInt(),
            string.substring(5, 7).toInt(),
            string.substring(8, 10).toInt()
        )
    }
}