package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface Root {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Login(val component: LoginComponent) : Child()
        data class Registration(val component: RegistrationComponent) : Child()
        data class Home(val component: HomeComponent) : Child()
    }
}