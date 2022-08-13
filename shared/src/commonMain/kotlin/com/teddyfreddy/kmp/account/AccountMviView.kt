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

        var optionalsShown: Boolean
    ) {
        // No-arg constructor for Swift.
        constructor() : this(
            email = ValidatedStringField(data = ""),
            password = ValidatedStringField(data = ""),
            passwordConfirmation = ValidatedStringField(data = ""),
            givenName = ValidatedStringField(data = ""),
            familyName = ValidatedStringField(data = ""),
            phone = "",
            dateOfBirth = null,

            optionalsShown = false
        )
    }

    sealed interface Event {
        data class ChangeField(val field: AccountField, val value: Any?, val validate: Boolean = false) : Event
        data class ValidateField(val field: AccountField) : Event
        object Cancel : Event
        object Continue : Event
    }

    val onLabel: suspend (label: AccountStore.Label) -> Unit
}


// Added for label delegation and Compose aggregation
open class AccountBaseMviView(
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    private val onRender: ((AccountMviView.Model) -> Unit)? = null // For AccountComposeViewModel
) : BaseMviView<AccountMviView.Model, AccountMviView.Event>(), AccountMviView {

    override val onLabel: suspend (label: AccountStore.Label) -> Unit = { // Invoked via bindings in binder
        when (it) {
            AccountStore.Label.Continue -> onContinue()
            AccountStore.Label.Cancel -> onCancel()
        }
    }

    override fun render(model: AccountMviView.Model) {
        onRender?.let {
            it(model)
        }
    }


    @Suppress("unused")
    fun changeField(field: String, value: Any?, validate: Boolean = false) {
        dispatch(AccountMviView.Event.ChangeField(AccountField.valueOf(field), value, validate))
    }

    @Suppress("unused")
    fun validateField(field: String) {
        dispatch(AccountMviView.Event.ValidateField(AccountField.valueOf(field)))
    }

    @Suppress("unused")
    fun cancelPressed() {
        dispatch(AccountMviView.Event.Cancel)
    }

    @Suppress("unused")
    fun continuePressed() {
        dispatch(AccountMviView.Event.Continue)
    }
}



