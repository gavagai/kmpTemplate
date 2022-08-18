package com.teddyfreddy.kmp.mvi.login

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.teddyfreddy.kmp.repository.AuthenticationRepository
import com.teddyfreddy.common.Field
import kotlinx.coroutines.launch

class LoginStoreFactory(
    private val storeFactory: StoreFactory
) {
    private sealed interface Msg {
        data class ChangeField(val field: LoginField, val value: Any?) : Msg
        data class ValidateField(val field: LoginField, val forceValid: Boolean? = false) : Msg
        data class SetFieldError(val field: LoginField, val error: String) : Msg
        data class SetEmailVerificationRequired(val required: Boolean = true): Msg
    }

    private sealed interface Action {
        data class SetEmailVerificationRequired(val required: Boolean = false): Action
    }

    fun create(): LoginStore =
        object : LoginStore,
            Store<LoginStore.Intent, LoginStore.State, LoginStore.Label> by storeFactory.create(
                name = "LoginStore",
                initialState = LoginStore.State(),
                bootstrapper = BootstrapperImpl(),
                executorFactory = LoginStoreFactory::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private object ReducerImpl : Reducer<LoginStore.State, Msg> {
        override fun LoginStore.State.reduce(msg: Msg): LoginStore.State =
            when (msg) {
                is Msg.ChangeField -> {
                    when (msg.field) {
                        LoginField.Username -> {
                            copy(
                                username = username.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        LoginField.Password -> {
                            copy(
                                password = password.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        LoginField.VerificationCode -> {
                            copy(
                                verificationCode = verificationCode.copy(
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
                    val forceValid = msg.forceValid != null && msg.forceValid
                    when (msg.field) {
                        LoginField.Username -> {
                            copy(
                                username = username.copy(
                                    error = if (forceValid) null else validator.validate(
                                        msg.field,
                                        username.data
                                    )
                                )
                            )
                        }
                        LoginField.Password -> {
                            copy(
                                password = password.copy(
                                    error = if (forceValid) null else validator.validate(
                                        msg.field,
                                        password.data
                                    )
                                )
                            )
                        }
                        LoginField.VerificationCode -> {
                            copy(
                                verificationCode = verificationCode.copy(
                                    error = if (forceValid) null else validator.validate(
                                        msg.field,
                                        verificationCode.data
                                    )
                                )
                            )
                        }
                    }
                }
                is Msg.SetFieldError -> {
                     when (msg.field) {
                        LoginField.Username -> {
                            copy(
                                username = username.copy(
                                    error = msg.error
                                )
                            )
                        }
                        LoginField.Password -> {
                            copy(
                                password = password.copy(
                                    error = msg.error
                                )
                            )
                        }
                        LoginField.VerificationCode -> {
                            copy(
                                verificationCode = verificationCode.copy(
                                    error = msg.error
                                )
                            )
                        }
                    }
                }
                is Msg.SetEmailVerificationRequired -> copy(emailVerificationRequired = msg.required)
            }
    }

    private class ExecutorImpl : CoroutineExecutor<LoginStore.Intent, Action, LoginStore.State, Msg, LoginStore.Label>() {
        override fun executeIntent(intent: LoginStore.Intent, getState: () -> LoginStore.State) =
            when (intent) {
                is LoginStore.Intent.Login -> executeLogin(getState)
                is LoginStore.Intent.ChangeField -> executeChangeField(intent)
                is LoginStore.Intent.ValidateField -> dispatch(Msg.ValidateField(intent.field, intent.forceValid))
                is LoginStore.Intent.SetFieldError -> dispatch(Msg.SetFieldError(intent.field, intent.error))
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
            dispatch(Msg.ValidateField(LoginField.VerificationCode))
            val state = getState()
            if (state.valid) {
                publish(LoginStore.Label.LoginInitiated)
                AuthenticationRepository.authenticate(
                    scope,
                    state.username.data,
                    state.password.data,
                    state.verificationCode.data
                ) { response, exception ->
                    publish(LoginStore.Label.LoginComplete(response, exception))
                }
            }
        }


        override fun executeAction(action: Action, getState: () -> LoginStore.State) =
            when (action) {
                is Action.SetEmailVerificationRequired -> dispatch(Msg.SetEmailVerificationRequired(action.required))
            }

    }


    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            val emailVerified = false // TODO: From shared preferences in reality
            scope.launch {
                dispatch(Action.SetEmailVerificationRequired(!emailVerified))
            }
        }
    }
}
