package com.teddyfreddy.kmp.android.ui.decompose

import androidx.compose.runtime.State
import com.teddyfreddy.kmp.mvi.login.LoginStore

interface Login {
    fun login()
    fun signup()

    val state: State<LoginStore.State>

    fun changeUsername(newVal: String)
    fun validateUsername()
    fun changePassword(newVal: String)
    fun validatePassword()
}