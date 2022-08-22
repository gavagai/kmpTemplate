package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.ComponentContext

class HomeComponent(
    componentContext: ComponentContext,
    private val onLogout: () -> Unit
) : Home, ComponentContext by componentContext {


    override fun logoutPressed() {
        onLogout()
    }

    override fun backPressed() {
        onLogout()
    }
}
