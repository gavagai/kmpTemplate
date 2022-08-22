package com.teddyfreddy.kmp.android.ui.decompose.registration

import androidx.compose.runtime.State
import com.teddyfreddy.kmp.mvi.account.AccountStore
import java.time.LocalDate

interface Account {
    val state: State<AccountStore.State>

    fun changeEmail(newVal: String)
    fun focusChangeEmail(focused: Boolean)
    fun changePassword(newVal: String)
    fun focusChangePassword(focused: Boolean)
    fun changePasswordConfirmation(newVal: String)
    fun focusChangePasswordConfirmation(focused: Boolean)
    fun changeFirstName(newVal: String)
    fun focusChangeFirstName(focused: Boolean)
    fun changeLastName(newVal: String)
    fun focusChangeLastName(focused: Boolean)
    fun changePhoneNumber(newVal: String)
    fun changeDateOfBirth(newVal: LocalDate?)

    fun cancelPressed()
    fun continuePressed()
}