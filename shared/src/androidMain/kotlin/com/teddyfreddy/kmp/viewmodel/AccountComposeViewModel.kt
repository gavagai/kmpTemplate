package com.teddyfreddy.kmp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.teddyfreddy.kmp.account.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AccountComposeViewModel(
    private val registrationContext: RegistrationContext
) : KoinComponent {
    private val binder = AccountController(AccountStoreFactory(get(), registrationContext).create())
    private val proxy = AccountBaseMviView(
        onContinue = {
            registrationContext.email = model.value.email.data
            registrationContext.givenName = model.value.givenName.data
            registrationContext.familyName = model.value.familyName.data

            // Navigate to next view
        },
        onCancel = {
            // Return from this view
        },
        onRender = {
            model.value = it
        }
    )

    var model: MutableState<AccountMviView.Model> = mutableStateOf(AccountMviView.Model())


    // Lifecycle events
    fun onViewCreated() {
        binder.onViewCreated(proxy)
    }

    fun onViewStarted() {
        binder.onStart()
    }

    fun onViewStopped() {
        binder.onStop()
    }

    fun onViewDestroyed() {
        binder.onViewDestroyed()
    }

    fun onDestroy() {
        binder.onDestroy()
    }
    // End Lifecycle events


    fun changeField(field: AccountField, value: Any?, validate: Boolean = false) {
        proxy.dispatch(AccountMviView.Event.ChangeField(field, value, validate))
    }

    fun validateField(field: AccountField) {
        proxy.dispatch(AccountMviView.Event.ValidateField(field))
    }

    fun cancelPressed() {
        proxy.dispatch(AccountMviView.Event.Cancel)
    }

    fun continuePressed() {
        proxy.dispatch(AccountMviView.Event.Forward)
    }
}
