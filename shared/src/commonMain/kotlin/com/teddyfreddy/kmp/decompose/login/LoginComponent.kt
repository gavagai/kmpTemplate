package com.teddyfreddy.kmp.decompose.login

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.teddyfreddy.common.network.NetworkRequestError
import com.teddyfreddy.kmp.mvi.login.LoginField
import com.teddyfreddy.kmp.mvi.login.LoginStore
import com.teddyfreddy.kmp.mvi.login.LoginStoreFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent

class LoginComponent(
    componentContext: ComponentContext,
    private val onLogin: () -> Unit,
    private val onSignup: () -> Unit
) : Login, ComponentContext by componentContext, KoinComponent {

//    private val preferences: SharedPreferences by inject()

    private val store = LoginStoreFactory(
                            DefaultStoreFactory(),
                            "normal",//preferences.getString(SharedPreferenceKeys.RecentUsername.key, null),
                            false//preferences.getBoolean(SharedPreferenceKeys.EmailVerified.key, false)
                        ).create()

    private var _model: MutableValue<Login.Model> = MutableValue(Login.Model())
    override val model: Value<Login.Model> = _model

    private val scope = CoroutineScope(Dispatchers.Main)
    init {
        scope.launch {
            store.states.distinctUntilChanged().map(stateToModel).collect {
                this@LoginComponent._model.value = it
            }
        }
        scope.launch {
            store.labels.collect {
                when (it) {
                    is LoginStore.Label.LoginInitiated -> { /* trigger waiting view */ }
                    is LoginStore.Label.LoginComplete -> executeLoginComplete(it.throwable)
                    is LoginStore.Label.EmailVerificationCodeSent -> executeEmailVerificationSent(it.throwable)
                }
            }
        }
        lifecycle.doOnDestroy { scope.cancel() }
    }

    private fun executeLoginComplete(throwable: Throwable?) {
        if (throwable == null) {
            recordRecentLogin(emailVerified = true)
            store.accept(LoginStore.Intent.SetEmailVerificationRequired(false))
            this@LoginComponent.onLogin()
        }
        else {
            recordRecentLogin(emailVerified = false)
            store.accept(LoginStore.Intent.SetEmailVerificationRequired(true))
            val errorMessage = when (throwable) {
                is NetworkRequestError -> {
                    when (throwable) {
                        NetworkRequestError.EmailVerificationFailed, NetworkRequestError.EmailVerificationCodeExpired -> {
                            setEmailVerificationCodeError(throwable.failureReason!!)
                        }
                        else -> {}
                    }
                    "${throwable.failureReason!!}${if (throwable.recoverySuggestion != null) " - ${throwable.recoverySuggestion!!}" else ""}"
                }
                else -> throwable.message
            }
            this@LoginComponent.onError(errorMessage)
        }
    }

    private fun recordRecentLogin(emailVerified: Boolean) {
//        with(preferences.edit()) {
//            putBoolean(SharedPreferenceKeys.EmailVerified.key, emailVerified)
//            putString(
//                SharedPreferenceKeys.RecentUsername.key,
//                store.state.username.data
//            )
//            apply()
//        }
    }

    private fun executeEmailVerificationSent(throwable: Throwable?) {
        val message: String =
            if (throwable != null) {
                when (throwable) {
                    is NetworkRequestError -> {
                        "${throwable.failureReason!!}${if (throwable.recoverySuggestion != null) " - ${throwable.recoverySuggestion!!}" else ""}"
                    }
                    else -> {
                        throwable.message ?: "Unknown error encountered - please try again"
                    }
                }
            }
            else {
                "Check your email for a new verification code - don't forget the Junk folder"
            }
        this@LoginComponent.onEmailVerificationCodeSent(message)
    }


    private lateinit var onError: (errorMessage: String?) -> Unit
    override fun login(onError: (String?) -> Unit) {
        this.onError = onError
        store.accept(LoginStore.Intent.Login)
    }

    override fun signup() {
        this.onSignup()
    }

    private lateinit var onEmailVerificationCodeSent: (message: String) -> Unit
    override fun getNewCode(onEmailVerificationCodeSent: (message: String) -> Unit) {
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

internal val stateToModel: LoginStore.State.() -> Login.Model =
    {
        Login.Model(
            username = username.copy(),
            password = password.copy(),
            verificationCode = verificationCode.copy(),
            emailVerificationRequired = emailVerificationRequired
        )
    }
