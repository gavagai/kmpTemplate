package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.teddyfreddy.kmp.mvi.RegistrationContext
import kotlinx.parcelize.Parcelize

class RegistrationComponent(
    componentContext: ComponentContext,
    private val onFinish: (String?) -> Unit
) : Registration, ComponentContext by componentContext {

    private var registrationContext: RegistrationContext = RegistrationContext()

    private val navigation = StackNavigation<Config>()
    private val stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Account,
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
            registrationContext = registrationContext,
            onContinue = { navigation.push(Config.Choice) },
            onCancel = { finish(null) }
        )

    private fun choiceComponent(componentContext: ComponentContext): ChoiceComponent =
        ChoiceComponent(
            componentContext = componentContext,
            registrationContext = registrationContext,
            onContinue = { choice -> /*navigation.push()*/ },
            onCancel = { finish(null) },
            onBack = { navigation.pop() }
        )

    override val childStack: Value<ChildStack<*, Registration.Child>>
        get() = stack


    private sealed class Config : Parcelable {
        @Parcelize
        object Account : Config()

        @Parcelize
        object Choice : Config()
    }



    override fun finish(username: String?) {
        this.onFinish(username)
    }
}