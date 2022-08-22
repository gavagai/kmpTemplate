package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.ComponentContext
import com.teddyfreddy.kmp.mvi.RegistrationContext

class ChoiceComponent (
    componentContext: ComponentContext,
    private val registrationContext: RegistrationContext,
    private val onContinue: (Choice.Destination) -> Unit,
    private val onCancel: () -> Unit,
    private val onBack: () -> Unit

) : Choice, ComponentContext by componentContext {

    override fun continuePressed() {
        onContinue(Choice.Destination.AskToJoin)
    }

    override fun cancelPressed() {
        onCancel()
    }

    override fun backPressed() {
        onBack()
    }

}