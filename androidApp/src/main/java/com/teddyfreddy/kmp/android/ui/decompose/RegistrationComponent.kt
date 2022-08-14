package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.ComponentContext

class RegistrationComponent(
    componentContext: ComponentContext,
    private val onFinish: (String?) -> Unit
) : Registration, ComponentContext by componentContext {

    override fun finish(username: String?) {
        this.onFinish(username)
    }
}