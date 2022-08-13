package com.teddyfreddy.kmp.account

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
        var email: ValidatedStringField = ValidatedStringField(data = ""),
        var password: ValidatedStringField = ValidatedStringField(data = ""),
        var passwordConfirmation: ValidatedStringField = ValidatedStringField(data = ""),
        var givenName: ValidatedStringField = ValidatedStringField(data = ""),
        var familyName: ValidatedStringField = ValidatedStringField(data = ""),
        var phone: String = "",
        var dateOfBirth: LocalDate? = null,

        val valid: Boolean =
                    email.error == null &&
                    password.error == null &&
                    passwordConfirmation.error == null &&
                    givenName.error == null &&
                    familyName.error == null
    )

    sealed interface Label {
        object Cancel: Label
        object Continue: Label
    }
}

