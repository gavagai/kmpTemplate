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
    private val registrationContext: RegistrationContext,
    private val onContinue: () -> Unit,
    private val onCancel: () -> Unit
) : Account, ComponentContext by componentContext {

    private val store = AccountStoreFactory(DefaultStoreFactory(), registrationContext).create()

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
                    is AccountStore.Label.Continue -> executeContinue()
                    is AccountStore.Label.Cancel -> this@AccountComponent.onCancel()
                }
            }
        }
        lifecycle.doOnDestroy { scope.cancel() }
    }

    private fun executeContinue() {
        registrationContext.email = state.value.email.data
        registrationContext.familyName = state.value.familyName.data
        registrationContext.givenName = state.value.givenName.data
        this@AccountComponent.onContinue()
    }


    override fun changeEmail(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.Email, newVal, false))
    }
    override fun focusChangeEmail(focused: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.Email, focused && store.state.email.data.isEmpty()))
    }
    override fun changePassword(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.Password, newVal, false))
    }
    override fun focusChangePassword(focused: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.Password, focused && store.state.password.data.isEmpty()))
    }
    override fun changePasswordConfirmation(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.PasswordConfirmation, newVal, false))
    }
    override fun focusChangePasswordConfirmation(focused: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.PasswordConfirmation, focused && store.state.passwordConfirmation.data.isEmpty()))
    }
    override fun changeFirstName(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.FirstName, newVal, false))
    }
    override fun focusChangeFirstName(focused: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.FirstName, focused && store.state.givenName.data.isEmpty()))
    }
    override fun changeLastName(newVal: String) {
        store.accept(AccountStore.Intent.ChangeField(AccountField.LastName, newVal, false))
    }
    override fun focusChangeLastName(focused: Boolean) {
        store.accept(AccountStore.Intent.ValidateField(AccountField.LastName, focused && store.state.familyName.data.isEmpty()))
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
