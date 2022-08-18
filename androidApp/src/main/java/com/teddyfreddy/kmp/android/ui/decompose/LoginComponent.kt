package com.teddyfreddy.kmp.android.ui.decompose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.teddyfreddy.kmp.mvi.login.LoginField
import com.teddyfreddy.kmp.mvi.login.LoginStore
import com.teddyfreddy.kmp.mvi.login.LoginStoreFactory
import com.teddyfreddy.common.network.NetworkResponse
import com.teddyfreddy.kmp.repository.LoginResponseDTO
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalCoroutinesApi::class)
class LoginComponent(
    componentContext: ComponentContext,
    private val onLogin: (response: NetworkResponse<LoginResponseDTO>?, exception: Throwable?) -> Unit,
    private val onSignup: () -> Unit
) : Login, ComponentContext by componentContext {

    private val store = LoginStoreFactory(DefaultStoreFactory()).create()

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
                    is LoginStore.Label.LoginInitiated -> { }
                    is LoginStore.Label.LoginComplete -> {
                        if (it.exception != null) {

                        }
                        this@LoginComponent.onLoginComplete(it.exception)
                        this@LoginComponent.onLogin(it.response, it.exception)
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

    override fun changeUsername(newVal: String) {
        store.accept(LoginStore.Intent.ChangeField(LoginField.Username, newVal, false))
    }
    override fun validateUsername(forceValid: Boolean) {
        store.accept(LoginStore.Intent.ValidateField(LoginField.Username, forceValid))
    }
    override fun changePassword(newVal: String) {
        store.accept(LoginStore.Intent.ChangeField(LoginField.Password, newVal, false))
    }
    override fun validatePassword(forceValid: Boolean) {
        store.accept(LoginStore.Intent.ValidateField(LoginField.Password, forceValid))
    }
    override fun changeVerificationCode(newVal: String) {
        store.accept(LoginStore.Intent.ChangeField(LoginField.VerificationCode, newVal, false))
    }
    override fun validateVerificationCode(forceValid: Boolean) {
        store.accept(LoginStore.Intent.ValidateField(LoginField.VerificationCode, forceValid))
    }
    override fun setEmailVerificationCodeError(error: String) {
        store.accept(LoginStore.Intent.SetFieldError(LoginField.VerificationCode, error))
    }
}

