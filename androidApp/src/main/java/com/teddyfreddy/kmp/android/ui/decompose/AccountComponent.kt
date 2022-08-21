package com.teddyfreddy.kmp.android.ui.decompose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.teddyfreddy.kmp.mvi.RegistrationContext
import com.teddyfreddy.kmp.mvi.account.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class AccountComponent(
    componentContext: ComponentContext,
    private val onContinue: () -> Unit,
    private val onCancel: () -> Unit
) : Account, ComponentContext by componentContext {

    private val store = AccountStoreFactory(DefaultStoreFactory(), RegistrationContext()).create()

    private var _state: MutableState<AccountStore.State> = mutableStateOf(store.state)
    override val state = _state


    private val scope = CoroutineScope(Dispatchers.Main)
    init {
        scope.launch {
            store.states.distinctUntilChanged().collect {
                this@AccountComponent._state.value = it
            }
        }
        scope.launch {
            store.labels.collect {
                when (it) {
                    is AccountStore.Label.Continue -> this@AccountComponent.onContinue()
                    is AccountStore.Label.Cancel -> this@AccountComponent.onCancel()
                }
            }
        }
        lifecycle.doOnDestroy { scope.cancel() }
    }

    override fun changeEmail(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.Username, newVal, false))
    }
    override fun validateEmail(forceValid: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.Username, forceValid))
    }
    override fun changePassword(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.Password, newVal, false))
    }
    override fun validatePassword(forceValid: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.Password, forceValid))
    }
    override fun changePasswordConfirmation(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.PasswordConfirmation, newVal, false))
    }
    override fun validatePasswordConfirmation(forceValid: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.PasswordConfirmation, forceValid))
    }
    override fun changeFirstName(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.FirstName, newVal, false))
    }
    override fun validateFirstName(forceValid: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.FirstName, forceValid))
    }
    override fun changeLastName(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.LastName, newVal, false))
    }
    override fun validateLastName(forceValid: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.LastName, forceValid))
    }
    override fun changePhoneNumber(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.PhoneNumber, newVal))
    }
    override fun changeDateOfBirth(newVal: LocalDate?) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.DateOfBirth, newVal))
    }


    override fun cancelPressed() {
        store.accept(AccountStore.Intent.Cancel)
    }

    override fun continuePressed() {
        store.accept(AccountStore.Intent.Continue)
    }

}
