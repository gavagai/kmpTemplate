package com.teddyfreddy.kmp.android.ui.decompose

import androidx.compose.runtime.State
import com.teddyfreddy.kmp.mvi.account.AccountField
import com.teddyfreddy.kmp.mvi.account.AccountStore

interface Account {
    val state: State<AccountStore.State>

    fun changeField(field: AccountField, value: Any?, validate: Boolean = false)
    fun validateField(field: AccountField)
    fun cancelPressed()
    fun continuePressed()
}