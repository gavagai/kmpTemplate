package com.teddyfreddy.kmp.android.ui.decompose

interface Choice {
    enum class Destination {
        AskToJoin,
        AcceptInvitation,
        CreateOrganization
        ;
    }

    fun continuePressed()
    fun cancelPressed()
    fun backPressed()
}