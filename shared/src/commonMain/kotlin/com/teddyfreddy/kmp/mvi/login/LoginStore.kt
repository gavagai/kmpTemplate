package com.teddyfreddy.kmp.mvi.login

import com.arkivanov.mvikotlin.core.store.Store
import com.teddyfreddy.common.ValidatedStringField
import com.teddyfreddy.common.network.NetworkResponse
import com.teddyfreddy.kmp.repository.LoginResponseDTO

interface LoginStore : Store<LoginStore.Intent, LoginStore.State, LoginStore.Label> {

    sealed interface Intent {
        data class ChangeField(val field: LoginField, val value: Any?, val validate: Boolean = false) : Intent
        data class ValidateField(val field: LoginField, val forceValid: Boolean? = false) : Intent
        data class SetFieldError(val field: LoginField, val error: String) : Intent
        data class SetEmailVerificationRequired(val required: Boolean) : Intent
        object Login : Intent
        object SendEmailVerificationCode: Intent
    }

    data class State(
        val username: ValidatedStringField,
        val password: ValidatedStringField,
        val verificationCode: ValidatedStringField,
        val emailVerificationRequired: Boolean
    ) {
        constructor() : this(
            username = ValidatedStringField(data = ""),
            password = ValidatedStringField(data = ""),
            verificationCode = ValidatedStringField(data = ""),
            emailVerificationRequired = false
        )

        val valid: Boolean
            get() = username.error == null &&
                    password.error == null &&
                    (!emailVerificationRequired || verificationCode.error == null)
    }


    sealed interface Label {
        object LoginInitiated: Label
        data class LoginComplete(
            val response: NetworkResponse<LoginResponseDTO>?,
            val throwable: Throwable?
        ): Label
        data class EmailVerificationCodeSent(
            val response: NetworkResponse<Unit>?,
            val throwable: Throwable?
        ): Label
    }
}

