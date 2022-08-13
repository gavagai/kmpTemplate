package com.teddyfreddy.kmp.account

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.MviView
import com.teddyfreddy.kmp.ValidatedStringField
import kotlinx.datetime.LocalDate

data class RegistrationContext(
    var email: String? = null,
    var givenName: String? = null,
    var familyName: String? = null
)


interface AccountMviView : MviView<AccountMviView.Model, AccountMviView.Event> {

    data class Model(
        var email: ValidatedStringField,
        var password: ValidatedStringField,
        var passwordConfirmation: ValidatedStringField,
        var givenName: ValidatedStringField,
        var familyName: ValidatedStringField,
        var phone: String,
        var dateOfBirth: LocalDate?,
    ) {
        // No-arg constructor for Swift.
        constructor() : this(
            email = ValidatedStringField(data = ""),
            password = ValidatedStringField(data = ""),
            passwordConfirmation = ValidatedStringField(data = ""),
            givenName = ValidatedStringField(data = ""),
            familyName = ValidatedStringField(data = ""),
            phone = "",
            dateOfBirth = null
        )
    }

    sealed interface Event {
        data class ChangeField(val field: AccountField, val value: Any?, val validate: Boolean = false) : Event
        data class ValidateField(val field: AccountField) : Event
        object Cancel : Event
        object Continue : Event
    }

    fun changeField(field: AccountField, value: Any?, validate: Boolean = false)
    fun validateField(field: AccountField)
    fun cancelPressed()
    fun continuePressed()

    val onLabel: suspend (label: AccountStore.Label) -> Unit
    fun onContinue()
    fun onCancel()
}


// Added for label delegation and Compose aggregation
open class AccountBaseMviView : BaseMviView<AccountMviView.Model, AccountMviView.Event>(), AccountMviView {

    override val onLabel: suspend (label: AccountStore.Label) -> Unit = { // Invoked via bindings in binder
        when (it) {
            AccountStore.Label.Continue -> this.onContinue()
            AccountStore.Label.Cancel -> this.onCancel()
        }
    }

    override fun onContinue() {}
    override fun onCancel() {}

    @Suppress("unused")
    override fun changeField(field: AccountField, value: Any?, validate: Boolean) {
        dispatch(AccountMviView.Event.ChangeField(field, value, validate))
    }

    @Suppress("unused")
    override fun validateField(field: AccountField) {
        dispatch(AccountMviView.Event.ValidateField(field))
    }

    @Suppress("unused")
    override fun cancelPressed() {
        dispatch(AccountMviView.Event.Cancel)
    }

    @Suppress("unused")
    override fun continuePressed() {
        dispatch(AccountMviView.Event.Continue)
    }
}



