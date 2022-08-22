package com.teddyfreddy.kmp.android.ui.decompose.app

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.teddyfreddy.kmp.android.ui.decompose.login.LoginComponent
import com.teddyfreddy.kmp.android.ui.decompose.registration.RegistrationComponent

interface Root {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Login(val component: LoginComponent) : Child()
        data class Registration(val component: RegistrationComponent) : Child()
        data class Home(val component: HomeComponent) : Child()
    }
}