package com.teddyfreddy.kmp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class AccountComposeViewModel(
    private val registrationContext: RegistrationContext
) : KoinComponent {
    private val binder = AccountBinder(AccountStoreProvider(get(), registrationContext).provide())
    private val proxy = AccountMVIViewProxy(
        onRender = {
            model.value = it
        },
        onForward = {
            registrationContext.email = model.value.email.data
            registrationContext.givenName = model.value.givenName.data
            registrationContext.familyName = model.value.familyName.data

            // Navigate to next view
        },
        onCancel = {
            // Return from this view
        }
    )

    var model: MutableState<AccountMVIView.Model> = mutableStateOf(AccountMVIView.Model())


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
        proxy.dispatch(AccountMVIView.Event.ChangeField(field, value, validate))
    }

    fun validateField(field: AccountViewField) {
        proxy.dispatch(AccountMVIView.Event.ValidateField(field))
    }

    fun cancelPressed() {
        proxy.dispatch(AccountMVIView.Event.Cancel)
    }

    fun continuePressed() {
        proxy.dispatch(AccountMVIView.Event.Forward)
    }
}
