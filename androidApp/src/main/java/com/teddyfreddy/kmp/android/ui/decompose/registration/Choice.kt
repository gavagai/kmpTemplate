package com.teddyfreddy.kmp.android.ui.decompose.registration

interface Choice {
    enum class Destination {
        AskToJoin,
        AcceptInvitation,
        CreateOrganization
        ;
    }

    fun askToJoinPressed()
    fun createOrganizationPressed()

    fun cancelPressed()
    fun backPressed()
}