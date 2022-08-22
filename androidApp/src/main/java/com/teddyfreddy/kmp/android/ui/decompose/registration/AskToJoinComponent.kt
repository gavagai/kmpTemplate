package com.teddyfreddy.kmp.android.ui.decompose.registration

import com.arkivanov.decompose.ComponentContext
import com.teddyfreddy.kmp.mvi.RegistrationContext

class AskToJoinComponent (
    componentContext: ComponentContext,
    private val registrationContext: RegistrationContext,
    private val onContinue: () -> Unit,
    private val onCancel: () -> Unit,
    private val onBack: () -> Unit

) : AskToJoin, ComponentContext by componentContext {

    override fun continuePressed() {
        onContinue()
    }

    override fun cancelPressed() {
        onCancel()
    }

    override fun backPressed() {
        onBack()
    }

}