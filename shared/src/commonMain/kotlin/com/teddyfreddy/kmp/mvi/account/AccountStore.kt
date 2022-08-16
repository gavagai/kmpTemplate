package com.teddyfreddy.kmp.mvi.account

import com.arkivanov.mvikotlin.core.store.Store
import com.teddyfreddy.kmp.ValidatedStringField
import kotlinx.datetime.LocalDate


interface AccountStore : Store<AccountStore.Intent, AccountStore.State, AccountStore.Label> {

    sealed interface Intent {
        data class ChangeField(val field: AccountField, val value: Any?, val validate: Boolean = false) : Intent
        data class ValidateField(val field: AccountField) : Intent
        object Cancel : Intent
        object Continue : Intent
    }

    data class State(
        val email: ValidatedStringField = ValidatedStringField(data = ""),
        val password: ValidatedStringField = ValidatedStringField(data = ""),
        val passwordConfirmation: ValidatedStringField = ValidatedStringField(data = ""),
        val givenName: ValidatedStringField = ValidatedStringField(data = ""),
        val familyName: ValidatedStringField = ValidatedStringField(data = ""),
        val phone: String = "",
        val dateOfBirth: LocalDate? = null,
    ) {
        val valid: Boolean
            get() =
                email.error == null &&
                password.error == null &&
                passwordConfirmation.error == null &&
                givenName.error == null &&
                familyName.error == null
    }

    sealed interface Label {
        object Cancel: Label
        object Continue: Label
    }
}

