package com.teddyfreddy.kmp.android.ui.registration

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.teddyfreddy.kmp.android.ui.decompose.Registration

@Composable
fun RegistrationView(
    component: Registration,
    modifier: Modifier? = Modifier
) {
    Children(component.childStack) {
        when (val child = it.instance) {
            is Registration.Child.Account -> AccountView(child.component)
            is Registration.Child.Choice -> ChoiceView(child.component)
        }
    }
}