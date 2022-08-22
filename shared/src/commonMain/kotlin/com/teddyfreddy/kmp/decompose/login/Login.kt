package com.teddyfreddy.kmp.decompose.login

import com.arkivanov.decompose.value.Value
import com.teddyfreddy.common.ValidatedStringField
import com.teddyfreddy.kmp.mvi.login.LoginStore

interface Login {
    data class Model(
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
    }
    val model: Value<Model>

    fun login(onError: (errorMessage: String?) -> Unit)
    fun signup()
    fun getNewCode(onEmailVerificationCodeSent: (message: String) -> Unit)

    fun changeUsername(newVal: String)
    fun focusChangeUsername(focused: Boolean)
    fun changePassword(newVal: String)
    fun focusChangePassword(focused: Boolean)
    fun changeVerificationCode(newVal: String)
    fun focusChangeVerificationCode(focused: Boolean)
    fun setEmailVerificationCodeError(error: String)
}