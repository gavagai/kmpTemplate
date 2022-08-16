package com.teddyfreddy.kmp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.teddyfreddy.kmp.mvi.account.*
import org.koin.core.component.KoinComponent

class AccountComposeViewModel(
    lifecycle: Lifecycle,
    private val registrationContext: RegistrationContext
) : KoinComponent {
    private val controller = AccountController(lifecycle, registrationContext)
    private val view = object: AccountBaseMviView() {
        override fun render(model: AccountMviView.Model) {
            this@AccountComposeViewModel.model.value = model
        }
        override fun onContinue() {
            this@AccountComposeViewModel.onContinue()
        }
        override fun onCancel() {
            this@AccountComposeViewModel.onCancel()
        }
    }

    var model: MutableState<AccountMviView.Model> = mutableStateOf(AccountMviView.Model())


    fun onViewCreated(viewLifecycle: Lifecycle) {
        controller.onViewCreated(view, viewLifecycle)
    }

    var continued = mutableStateOf(0)
    fun onContinue() {
        registrationContext.email = model.value.email.data
        registrationContext.givenName = model.value.givenName.data
        registrationContext.familyName = model.value.familyName.data

        // Navigate to next view
        continued.value = continued.value + 1
    }

    var canceled = mutableStateOf(0)
    fun onCancel() {
        // Return from this view
        canceled.value = canceled.value + 1
    }


    fun changeField(field: AccountField, value: Any?, validate: Boolean = false) {
        view.dispatch(AccountMviView.Event.ChangeField(field, value, validate))
    }

    fun validateField(field: AccountField) {
        view.dispatch(AccountMviView.Event.ValidateField(field))
    }

    fun cancelPressed() {
        view.dispatch(AccountMviView.Event.Cancel)
    }

    fun continuePressed() {
        view.dispatch(AccountMviView.Event.Continue)
    }
}
