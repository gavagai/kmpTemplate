package com.teddyfreddy.kmp.android.ui.compose.registration

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.teddyfreddy.kmp.decompose.registration.Registration

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun RegistrationView(
    component: Registration,
    modifier: Modifier? = Modifier
) {
    Children(
        stack = component.childStack,
        animation = stackAnimation(slide()),
    ) {
        when (val child = it.instance) {
            is Registration.Child.Account -> AccountView(child.component)
            is Registration.Child.Choice -> ChoiceView(child.component)
            is Registration.Child.AskToJoin -> AskToJoinView(child.component)
            is Registration.Child.CreateOrganization -> CreateOrganizationView(child.component)
            is Registration.Child.Congratulations -> CongratulationsView(child.component)
        }
    }
}