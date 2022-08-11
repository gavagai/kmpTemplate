package com.teddyfreddy.kmp.viewmodel

import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Suppress("unused") // Constructed in iOS
open class AccountSwiftUiViewModel(
    private val registrationContext: RegistrationContext
) : KoinComponent {
    private lateinit var binder: AccountBinder
    private lateinit var proxy: AccountMVIViewProxy
    private lateinit var model: AccountMVIViewModel

    init {
        runBlocking {
            this@AccountSwiftUiViewModel.binder = AccountBinder(AccountStoreProvider(get(), registrationContext).provide())
            this@AccountSwiftUiViewModel.proxy = AccountMVIViewProxy(
                onRender = {
                    model = it
                },
                onForward = {
                    registrationContext.email = model.email.data
                    registrationContext.givenName = model.givenName.data
                    registrationContext.familyName = model.familyName.data

                    // Navigate to next view
                },
                onCancel = {
                    // Return from this view
                }
            )
            this@AccountSwiftUiViewModel.model = AccountMVIViewModel()
        }
    }

    open fun onRender(model: AccountMVIViewModel) {
        this.model = model
    }


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


    fun changeField(field: String, value: Any?, validate: Boolean = false) {
        proxy.dispatch(AccountMVIView.Event.ChangeField(AccountViewField.valueOf(field), value, validate))
    }

    fun validateField(field: String) {
        proxy.dispatch(AccountMVIView.Event.ValidateField(AccountViewField.valueOf(field)))
    }

    fun cancelPressed() {
        proxy.dispatch(AccountMVIView.Event.Cancel)
    }

    fun continuePressed() {
        proxy.dispatch(AccountMVIView.Event.Forward)
    }
}
