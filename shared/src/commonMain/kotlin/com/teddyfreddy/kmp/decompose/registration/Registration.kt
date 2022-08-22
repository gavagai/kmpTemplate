package com.teddyfreddy.kmp.decompose.registration

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface Registration {
    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Account(val component: AccountComponent) : Child()
        data class Choice(val component: ChoiceComponent) : Child()
        data class AskToJoin(val component: AskToJoinComponent) : Child()
        data class CreateOrganization(val component: CreateOrganizationComponent) : Child()
        data class Congratulations(val component: CongratulationsComponent) : Child()
    }

    fun finish(username: String?)
}