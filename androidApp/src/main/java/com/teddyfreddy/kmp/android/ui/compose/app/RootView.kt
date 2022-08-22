package com.teddyfreddy.kmp.android.ui.compose.app

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.teddyfreddy.android.ui.adaptive.AdaptiveDesign
import com.teddyfreddy.kmp.android.ui.decompose.app.Root
import com.teddyfreddy.kmp.android.ui.decompose.app.RootComponent
import com.teddyfreddy.kmp.android.ui.compose.login.LoginView
import com.teddyfreddy.kmp.android.ui.compose.registration.RegistrationView

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RootView(
    component: RootComponent,
    navigationType: AdaptiveDesign.NavigationType,
    contentType: AdaptiveDesign.ContentType,
) {
    Children(component.childStack) {
        when (val child = it.instance) {
            is Root.Child.Login -> LoginView(child.component)
            is Root.Child.Registration -> RegistrationView(child.component)
            is Root.Child.Home -> HomeView(child.component)
        }
    }
}