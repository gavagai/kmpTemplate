package com.teddyfreddy.kmp.viewmodel.account

import com.teddyfreddy.kmp.mvi.account.AccountStore

internal val stateToModel: AccountStore.State.() -> AccountMviView.Model =
    {
        AccountMviView.Model(
            email = email,
            password = password,
            passwordConfirmation = passwordConfirmation,
            givenName = givenName,
            familyName = familyName,
            phone = phone,
            dateOfBirth = dateOfBirth
        )
    }

internal val eventToIntent: AccountMviView.Event.() -> AccountStore.Intent =
    {
        when (this) {
            is AccountMviView.Event.ChangeField -> AccountStore.Intent.ChangeField(
                field,
                value,
                validate
            )
            is AccountMviView.Event.ValidateField -> AccountStore.Intent.ValidateField(
                field,
                forceValid
            )
            AccountMviView.Event.Cancel -> AccountStore.Intent.Cancel
            AccountMviView.Event.Continue -> AccountStore.Intent.Continue
        }
    }
