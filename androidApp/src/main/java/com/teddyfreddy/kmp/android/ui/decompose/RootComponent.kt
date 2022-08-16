package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.teddyfreddy.kmp.mvi.account.RegistrationContext
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

class RootComponent(
    componentContext: ComponentContext
) : Root, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Login,
            handleBackButton = true, // Pop the back stack on back button press
            childFactory = ::createChild
        )

    override val childStack: Value<ChildStack<*, Root.Child>>
        get() = stack


    private fun createChild(config: Config, componentContext: ComponentContext): Root.Child =
        when (config) {
            is Config.Login -> Root.Child.Login(component = loginComponent(componentContext))
            is Config.Registration -> Root.Child.Registration(component = registrationComponent(componentContext))
            is Config.Home -> Root.Child.Home(component = homeComponent(componentContext))
        }

    private fun loginComponent(componentContext: ComponentContext): LoginComponent =
        LoginComponent(
            componentContext = componentContext,
            onLogin = { response, message ->
                if (response != null) navigation.replaceCurrent(configuration = Config.Home)
            },
            onSignup = { navigation.replaceCurrent(configuration = Config.Registration()) }
        )

    private fun registrationComponent(componentContext: ComponentContext): RegistrationComponent =
        RegistrationComponent(
            componentContext = componentContext,
            onFinish = { navigation.replaceCurrent(configuration = Config.Login) }
        )

    private fun homeComponent(componentContext: ComponentContext): HomeComponent =
        HomeComponent(
            componentContext = componentContext,
            onLogout = { navigation.replaceCurrent(configuration = Config.Login) },
            onBack = { navigation.replaceCurrent(configuration = Config.Login) }
        )



    private sealed class Config : Parcelable {
        @Parcelize
        object Login : Config()

        @Parcelize
        data class Registration(val registrationContext: @RawValue RegistrationContext = RegistrationContext()) : Config()

        @Parcelize
        object Home : Config()
    }
}