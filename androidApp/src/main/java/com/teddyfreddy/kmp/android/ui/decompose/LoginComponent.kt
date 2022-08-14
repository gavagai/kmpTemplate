package com.teddyfreddy.kmp.android.ui.decompose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.teddyfreddy.kmp.login.LoginField
import com.teddyfreddy.kmp.login.LoginStore
import com.teddyfreddy.kmp.login.LoginStoreFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalCoroutinesApi::class)
class LoginComponent(
    componentContext: ComponentContext,
    private val onLogin: () -> Unit,
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
                    is LoginStore.Label.Login -> this@LoginComponent.onLogin()
                }
            }
        }
        lifecycle.doOnDestroy { scope.cancel() }
    }


    override fun login() {
        store.accept(LoginStore.Intent.Login)
    }

    override fun signup() {
        this.onSignup()
    }

    override fun changeUsername(newVal: String) {
        store.accept(LoginStore.Intent.ChangeField(LoginField.Username, newVal, false))
    }
    override fun validateUsername() {
        store.accept(LoginStore.Intent.ValidateField(LoginField.Username))
    }
    override fun changePassword(newVal: String) {
        store.accept(LoginStore.Intent.ChangeField(LoginField.Password, newVal, false))
    }
    override fun validatePassword() {
        store.accept(LoginStore.Intent.ValidateField(LoginField.Password))
    }
}