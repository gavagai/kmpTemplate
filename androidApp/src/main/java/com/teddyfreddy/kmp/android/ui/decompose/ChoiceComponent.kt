package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.ComponentContext

class ChoiceComponent (
    componentContext: ComponentContext,
    private val onContinue: () -> Unit,
    private val onCancel: () -> Unit,
    private val onBack: () -> Unit

) : Choice, ComponentContext by componentContext {

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