package com.teddyfreddy.kmp.login

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.teddyfreddy.kmp.network.NetworkRequest
import com.teddyfreddy.kmp.network.NetworkSession
import com.teddyfreddy.kmp.viewmodel.Field
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch

class LoginStoreFactory(
    private val storeFactory: StoreFactory
) {
    private sealed interface Msg {
        data class ChangeField(val field: LoginField, val value: Any?) : Msg
        data class ValidateField(val field: LoginField) : Msg
    }

    fun create(): LoginStore =
        object : LoginStore,
            Store<LoginStore.Intent, LoginStore.State, LoginStore.Label> by storeFactory.create(
                name = "LoginStore",
                initialState = LoginStore.State(),
                executorFactory = LoginStoreFactory::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private object ReducerImpl : Reducer<LoginStore.State, Msg> {
        override fun LoginStore.State.reduce(msg: Msg): LoginStore.State =
            when (msg) {
                is Msg.ChangeField -> {
                    when (msg.field) {
                        LoginField.Username -> {
                            val username = username.copy(
                                data = msg.value as? String ?: ""
                            )
                            copy(
                                username = username
                            )
                        }
                        LoginField.Password -> {
                            copy(
                                password = password.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                    }
                }
                is Msg.ValidateField -> {
                    val validator = msg.field.validator ?: Field.Validator { _, _, _ ->
                        null
                    }
                    when (msg.field) {
                        LoginField.Username -> {
                            copy(
                                username = username.copy(
                                    error = validator.validate(msg.field, username.data)
                                )
                            )
                        }
                        LoginField.Password -> {
                            copy(
                                password = password.copy(
                                    error = validator.validate(msg.field, password.data)
                                )
                            )
                        }
                    }
                }
            }
    }

    private class ExecutorImpl : CoroutineExecutor<LoginStore.Intent, Nothing, LoginStore.State, Msg, LoginStore.Label>() {
        override fun executeIntent(intent: LoginStore.Intent, getState: () -> LoginStore.State) =
            when (intent) {
                is LoginStore.Intent.Login -> executeLogin(getState)
                is LoginStore.Intent.ChangeField -> executeChangeField(intent)
                is LoginStore.Intent.ValidateField -> dispatch(Msg.ValidateField(intent.field))
            }

        private fun executeChangeField(intent: LoginStore.Intent.ChangeField) {
            dispatch(Msg.ChangeField(intent.field, intent.value))
            if (intent.validate) {
                dispatch(Msg.ValidateField(intent.field))
            }
        }

        private fun executeLogin(getState: () -> LoginStore.State) {
            dispatch(Msg.ValidateField(LoginField.Username))
            dispatch(Msg.ValidateField(LoginField.Password))
            val state = getState()
            if (state.valid) {
                scope.launch {
                    NetworkSession.execute<LoginDTO>(
                        NetworkRequest("http://localhost:8080/login") {
                            this.method = HttpMethod.Post
                            this.header(
                                "Authorization",
                                NetworkSession.authorizationHeader(
                                    state.username.data,
                                    state.password.data
                                )
                            )
                            this.header("Content-Type", "application/json")
                        }
                    )
                        .collect {
                            print("booyah")
                        }
                }
                publish(LoginStore.Label.Login(state.username.data, state.password.data))
            }
        }
    }
}
/*
guard let url = URL(string: serviceUrl(resource: "login"))
else { preconditionFailure("Invalid URL format") }
var request = URLRequest(url: url)
request.httpMethod = "POST"
request.httpShouldHandleCookies = true
request.setValue(authorizationValue(accessToken), forHTTPHeaderField: "Authorization")
request.setValue("application/json", forHTTPHeaderField: "Content-Type")
do {
    request.httpBody = try JSONEncoderWithExposedDates().encode(loginAPIRequest)
    }
catch { preconditionFailure("Couldn't set JSON body") }
return request
*/