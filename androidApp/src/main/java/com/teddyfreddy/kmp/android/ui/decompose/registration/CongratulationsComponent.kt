package com.teddyfreddy.kmp.android.ui.decompose.registration

import com.arkivanov.decompose.ComponentContext

class CongratulationsComponent (
    componentContext: ComponentContext,
    private val onFinish: (Boolean) -> Unit

) : Congratulations, ComponentContext by componentContext {
    override fun finishPressed() {
        onFinish(true)
    }

}
