package com.teddyfreddy.kmp.android.ui.decompose.login

import androidx.compose.runtime.State
import com.teddyfreddy.kmp.mvi.login.LoginStore

interface Login {
    val state: State<LoginStore.State>

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