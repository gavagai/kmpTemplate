package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface Registration {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Account(val component: AccountComponent) : Child()
        data class Choice(val component: ChoiceComponent) : Child()
    }

    fun finish(username: String?)
}