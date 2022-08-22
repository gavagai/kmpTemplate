package com.teddyfreddy.kmp.decompose.app

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.teddyfreddy.kmp.decompose.registration.RegistrationComponent
import com.teddyfreddy.kmp.decompose.login.LoginComponent
import org.koin.core.component.KoinComponent

class RootComponent(
    componentContext: ComponentContext
) : Root, ComponentContext by componentContext, KoinComponent {

//    private val preferences: SharedPreferences by inject()

    private val navigation = StackNavigation<Config>()
    private val stack =
        childStack(
            source = navigation,
//            initialConfiguration = Config.Login(preferences.getString(SharedPreferenceKeys.RecentUsername.key, null)),
            initialConfiguration = Config.Login("normal"),
            handleBackButton = true, // Pop the back stack on back button press
            childFactory = ::createChild
        )

    override val childStack: Value<ChildStack<*, Root.Child>>
        get() = stack


    private fun createChild(config: Config, componentContext: ComponentContext): Root.Child =
        when (config) {
            is Config.Login -> Root.Child.Login(component = loginComponent(componentContext))
            is Config.Registration -> Root.Child.Registration(
                component = registrationComponent(
                    componentContext
                )
            )
            is Config.Home -> Root.Child.Home(component = homeComponent(componentContext))
        }

    private fun loginComponent(componentContext: ComponentContext): LoginComponent =
        LoginComponent(
            componentContext = componentContext,
            onLogin = { navigation.push(configuration = Config.Home) },
            onSignup = { navigation.push(configuration = Config.Registration) }
        )

    private fun registrationComponent(componentContext: ComponentContext): RegistrationComponent =
        RegistrationComponent(
            componentContext = componentContext,
            onFinish = {
//                if (it != null) {
//                    with(preferences.edit()) {
//                        putBoolean(SharedPreferenceKeys.EmailVerified.key, false)
//                        putString(SharedPreferenceKeys.RecentUsername.key, it)
//                        apply()
//                    }
//                }
                navigation.pop()
                navigation.replaceCurrent(Config.Login(it)) // Force new state
            }
        )

    private fun homeComponent(componentContext: ComponentContext): HomeComponent =
        HomeComponent(
            componentContext = componentContext,
            onLogout = { navigation.pop() },
        )



    private sealed class Config : Parcelable {
        @Parcelize
        data class Login(val username: String?) : Config()

        @Parcelize
        object Registration : Config()

        @Parcelize
        object Home : Config()
    }
}