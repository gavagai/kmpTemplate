package com.teddyfreddy.kmp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AccountComposeViewModel(
    private val registrationContext: RegistrationContext
) : KoinComponent {
    private val binder = AccountBinder(AccountStoreProvider(get(), registrationContext).provide())
    private val proxy = AccountViewProxy(
        onRender = {
            model.value = it
        },
        onForward = {
            registrationContext.email = model.value.email.value as? String
            registrationContext.givenName = model.value.givenName.value as? String
            registrationContext.familyName = model.value.familyName.value as? String

            // Navigate to next view
        },
        onCancel = {
            // Return from this view
        }
    )

    var model: MutableState<AccountView.Model> = mutableStateOf(AccountView.Model())


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


    fun changeField(field: AccountViewField, value: Any?, validate: Boolean = false) {
        proxy.dispatch(AccountView.Event.ChangeField(field, value, validate))
    }

    fun validateField(field: AccountViewField) {
        proxy.dispatch(AccountView.Event.ValidateField(field))
    }

    fun cancelPressed() {
        proxy.dispatch(AccountView.Event.Cancel)
    }

    fun continuePressed() {
        proxy.dispatch(AccountView.Event.Forward)
    }
}
