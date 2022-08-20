package com.teddyfreddy.kmp.viewmodel.login

import com.teddyfreddy.kmp.mvi.login.LoginStore

internal val stateToModel: LoginStore.State.() -> LoginMviView.Model =
    {
        LoginMviView.Model(
            username = username.copy(),
            password = password.copy(),
            verificationCode = verificationCode.copy(),
            emailVerificationRequired = emailVerificationRequired
        )
    }

internal val eventToIntent: LoginMviView.Event.() -> LoginStore.Intent =
    {
        when (this) {
            is LoginMviView.Event.ChangeField -> LoginStore.Intent.ChangeField(
                field,
                value,
                validate
            )
            is LoginMviView.Event.ValidateField -> LoginStore.Intent.ValidateField(
                field,
                forceValid
            )
            is LoginMviView.Event.SetFieldError -> LoginStore.Intent.SetFieldError(
                field,
                error
            )
            is LoginMviView.Event.SetEmailVerificationRequired -> LoginStore.Intent.SetEmailVerificationRequired(
                required
            )
            LoginMviView.Event.Login -> LoginStore.Intent.Login
            LoginMviView.Event.SendEmailVerificationCode -> LoginStore.Intent.SendEmailVerificationCode
        }
    }
