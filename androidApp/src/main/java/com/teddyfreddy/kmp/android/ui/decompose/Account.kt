package com.teddyfreddy.kmp.android.ui.decompose

import androidx.compose.runtime.State
import com.teddyfreddy.kmp.mvi.account.AccountField
import com.teddyfreddy.kmp.mvi.account.AccountStore
import java.time.LocalDate

interface Account {
    val state: State<AccountStore.State>

    fun changeEmail(newVal: String)
    fun validateEmail(forceValid: Boolean)
    fun changePassword(newVal: String)
    fun validatePassword(forceValid: Boolean)
    fun changePasswordConfirmation(newVal: String)
    fun validatePasswordConfirmation(forceValid: Boolean)
    fun changeFirstName(newVal: String)
    fun validateFirstName(forceValid: Boolean)
    fun changeLastName(newVal: String)
    fun validateLastName(forceValid: Boolean)
    fun changePhoneNumber(newVal: String)
    fun changeDateOfBirth(newVal: LocalDate?)

    fun cancelPressed()
    fun continuePressed()
}