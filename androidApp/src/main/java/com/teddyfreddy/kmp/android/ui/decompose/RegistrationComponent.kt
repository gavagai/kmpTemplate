package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.teddyfreddy.kmp.mvi.account.RegistrationContext
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

class RegistrationComponent(
    componentContext: ComponentContext,
    private val onFinish: (String?) -> Unit
) : Registration, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Account(),
            handleBackButton = true, // Pop the back stack on back button press
            childFactory = ::createChild
        )

    private fun createChild(config: Config, componentContext: ComponentContext): Registration.Child =
        when (config) {
            is Config.Account -> Registration.Child.Account(component = accountComponent(componentContext))
            is Config.Choice -> Registration.Child.Choice(component = choiceComponent(componentContext))
        }

    private fun accountComponent(componentContext: ComponentContext): AccountComponent =
        AccountComponent(
            componentContext = componentContext,
            onContinue = { navigation.push(Config.Choice()) },
            onCancel = { finish() }
        )

    private fun choiceComponent(componentContext: ComponentContext): ChoiceComponent =
        ChoiceComponent(
            componentContext = componentContext,
            onContinue = { /*navigation.push()*/ },
            onCancel = { finish() },
            onBack = { navigation.pop() }
        )

    private fun finish() {
        onFinish("admin") // TODO
    }

    override val childStack: Value<ChildStack<*, Registration.Child>>
        get() = stack


    private sealed class Config : Parcelable {
        @Parcelize
        data class Account(val registrationContext: @RawValue RegistrationContext = RegistrationContext()) : Config()

        @Parcelize
        data class Choice(val registrationContext: @RawValue RegistrationContext = RegistrationContext()) : Config()
    }



    override fun finish(username: String?) {
        this.onFinish(username)
    }
}