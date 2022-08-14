package com.teddyfreddy.kmp.android.ui.decompose

import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class LoginComponent(
    componentContext: ComponentContext,
    modifier: Modifier? = Modifier,
    private val onLogin: () -> Unit,
    private val onSignup: () -> Unit
) : Login, ComponentContext by componentContext {

    override val model: Value<Login.Model> = MutableValue(Login.Model())

    override fun login(username: String, password: String) {
        // Actual login activity

        // For navigation (replace by Home)
        this.onLogin()
    }

    override fun signup() {
        this.onSignup()
    }

    override fun onUsernameChange(newval: String) {
        model.value.username = newval
    }
    override fun onPasswordChange(newval: String) {
        model.value.password = newval
    }

}