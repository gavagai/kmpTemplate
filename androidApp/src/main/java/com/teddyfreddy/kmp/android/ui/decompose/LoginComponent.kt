package com.teddyfreddy.kmp.android.ui.decompose

import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.teddyfreddy.common.network.NetworkRequestError
import com.teddyfreddy.kmp.mvi.login.LoginField
import com.teddyfreddy.kmp.mvi.login.LoginStore
import com.teddyfreddy.kmp.mvi.login.LoginStoreFactory
import com.teddyfreddy.common.network.NetworkResponse
import com.teddyfreddy.kmp.android.SharedPreferenceKeys
import com.teddyfreddy.kmp.repository.LoginResponseDTO
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(ExperimentalCoroutinesApi::class)
class LoginComponent(
    componentContext: ComponentContext,
    private val onLogin: (response: NetworkResponse<LoginResponseDTO>?, exception: Throwable?) -> Unit,
    private val onSignup: () -> Unit
) : Login, ComponentContext by componentContext, KoinComponent {

    private val preferences: SharedPreferences by inject()

    private val store = LoginStoreFactory(
                            DefaultStoreFactory(),
                            preferences.getString(SharedPreferenceKeys.RecentUsername.key, null),
                            preferences.getBoolean(SharedPreferenceKeys.EmailVerified.key, false)
                        ).create()

    private var _state: MutableState<LoginStore.State> = mutableStateOf(store.state)
    override val state = _state

    private val scope = CoroutineScope(Dispatchers.Main)
    init {
        scope.launch {
            store.states.distinctUntilChanged().collect {
                this@LoginComponent._state.value = it
            }
        }
        scope.launch {
            store.labels.collect {
                when (it) {
                    is LoginStore.Label.LoginInitiated -> {}
                    is LoginStore.Label.LoginComplete -> {
                        if (it.exception == null) {
                            with(preferences.edit()) {
                                putBoolean(SharedPreferenceKeys.EmailVerified.key, true)
                                putString(
                                    SharedPreferenceKeys.RecentUsername.key,
                                    store.state.username.data
                                )
                                apply()
                            }
                            store.accept(LoginStore.Intent.SetEmailVerificationRequired(false))
                        } else if (it.exception is NetworkRequestError.EmailVerificationFailed ||
                            it.exception is NetworkRequestError.EmailVerificationCodeExpired
                        ) {
                            with(preferences.edit()) {
                                putBoolean(SharedPreferenceKeys.EmailVerified.key, false)
                                apply()
                            }
                            store.accept(LoginStore.Intent.SetEmailVerificationRequired(true))
                        }
                        this@LoginComponent.onLoginComplete(it.exception)
                        this@LoginComponent.onLogin(it.response, it.exception)
                    }
                    is LoginStore.Label.EmailVerificationCodeSent -> {
                        this@LoginComponent.onEmailVerificationCodeSent(it.exception)
                    }
                }
            }
        }
        lifecycle.doOnDestroy { scope.cancel() }
    }


    private lateinit var onLoginComplete: (exception: Throwable?) -> Unit
    override fun login(onLoginComplete: (exception: Throwable?) -> Unit) {
        this.onLoginComplete = onLoginComplete
        store.accept(LoginStore.Intent.Login)
    }

    override fun signup() {
        this.onSignup()
    }

    private lateinit var onEmailVerificationCodeSent: (exception: Throwable?) -> Unit
    override fun getNewCode(onEmailVerificationCodeSent: (exception: Throwable?) -> Unit) {
        this.onEmailVerificationCodeSent = onEmailVerificationCodeSent
        store.accept(LoginStore.Intent.SendEmailVerificationCode)
    }

    override fun changeUsername(newVal: String) {
        store.accept(LoginStore.Intent.ChangeField(LoginField.Username, newVal, false))
    }
    override fun focusChangeUsername(focused: Boolean) {
        store.accept(LoginStore.Intent.ValidateField(LoginField.Username, focused && store.state.username.data.isEmpty()))
    }
    override fun changePassword(newVal: String) {
        store.accept(LoginStore.Intent.ChangeField(LoginField.Password, newVal, false))
    }
    override fun focusChangePassword(focused: Boolean) {
        store.accept(LoginStore.Intent.ValidateField(LoginField.Password, focused && store.state.password.data.isEmpty()))
    }
    override fun changeVerificationCode(newVal: String) {
        store.accept(LoginStore.Intent.ChangeField(LoginField.VerificationCode, newVal, false))
    }
    override fun focusChangeVerificationCode(focused: Boolean) {
        store.accept(LoginStore.Intent.ValidateField(LoginField.VerificationCode, focused && store.state.verificationCode.data.isEmpty()))
    }
    override fun setEmailVerificationCodeError(error: String) {
        store.accept(LoginStore.Intent.SetFieldError(LoginField.VerificationCode, error))
    }
}

