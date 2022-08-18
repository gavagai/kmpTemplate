package com.teddyfreddy.kmp.android.ui.decompose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import com.teddyfreddy.kmp.mvi.login.LoginStore

interface Login {
    fun login(onLoginComplete: (exception: Throwable?) -> Unit)
    fun signup()

    val state: State<LoginStore.State>

    fun changeUsername(newVal: String)
    fun validateUsername(forceValid: Boolean)
    fun changePassword(newVal: String)
    fun validatePassword(forceValid: Boolean)
    fun changeVerificationCode(newVal: String)
    fun validateVerificationCode(forceValid: Boolean)
    fun setEmailVerificationCodeError(error: String)
}