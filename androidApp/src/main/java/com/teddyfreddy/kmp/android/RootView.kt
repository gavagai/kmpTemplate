package com.teddyfreddy.kmp.android

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.teddyfreddy.kmp.android.ui.app.HomeView
import com.teddyfreddy.kmp.android.ui.decompose.Root
import com.teddyfreddy.kmp.android.ui.decompose.RootComponent
import com.teddyfreddy.kmp.android.ui.login.LoginView
import com.teddyfreddy.kmp.android.ui.registration.RegistrationView

@Composable
fun RootView(
    component: RootComponent
) {
    Children(component.childStack) {
        when (val child = it.instance) {
            is Root.Child.Login -> LoginView(child.component)
            is Root.Child.Registration -> RegistrationView(child.component)
            is Root.Child.Home -> HomeView(child.component)
        }
    }
}