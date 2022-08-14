package com.teddyfreddy.kmp.login

import com.arkivanov.mvikotlin.core.store.Store
import com.teddyfreddy.kmp.ValidatedStringField

interface LoginStore : Store<LoginStore.Intent, LoginStore.State, LoginStore.Label> {

    sealed interface Intent {
        data class ChangeField(val field: LoginField, val value: Any?, val validate: Boolean = false) : Intent
        data class ValidateField(val field: LoginField) : Intent
        object Login : Intent
    }

    data class State(
        var username: ValidatedStringField = ValidatedStringField(data = ""),
        var password: ValidatedStringField = ValidatedStringField(data = ""),

        val valid: Boolean =
            username.error == null &&
            password.error == null
    )

    sealed interface Label {
        data class Login(
            val username: String,
            val password: String
        ): Label
    }
}
