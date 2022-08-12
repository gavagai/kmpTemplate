package com.teddyfreddy.kmp.viewmodel

import com.teddyfreddy.kmp.account.*
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@Suppress("unused") // Constructed in iOS
open class AccountSwiftUiViewModel(
    private val registrationContext: RegistrationContext
) : KoinComponent {
    private lateinit var binder: AccountController
    private lateinit var proxy: AccountBaseMviView
    private lateinit var model: AccountMviView.Model

    init {
        runBlocking {
            this@AccountSwiftUiViewModel.binder = AccountController(AccountStoreFactory(get(), registrationContext).create())
            this@AccountSwiftUiViewModel.proxy = AccountBaseMviView(
                onContinue = {
                    registrationContext.email = model.email.data
                    registrationContext.givenName = model.givenName.data
                    registrationContext.familyName = model.familyName.data

                    // Navigate to next view
                },
                onCancel = {
                    // Return from this view
                },
                onRender = {
                    this@AccountSwiftUiViewModel.render(it)
                }
            )
            this@AccountSwiftUiViewModel.model = AccountMviView.Model()
        }
    }

    open fun render(model: AccountMviView.Model) {
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
        proxy.dispatch(AccountMviView.Event.ChangeField(AccountField.valueOf(field), value, validate))
    }

    fun validateField(field: String) {
        proxy.dispatch(AccountMviView.Event.ValidateField(AccountField.valueOf(field)))
    }

    fun cancelPressed() {
        proxy.dispatch(AccountMviView.Event.Cancel)
    }

    fun continuePressed() {
        proxy.dispatch(AccountMviView.Event.Forward)
    }
}
