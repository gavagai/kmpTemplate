package com.teddyfreddy.kmp.viewmodel.account

import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.MviView
import com.teddyfreddy.common.ValidatedStringField
import com.teddyfreddy.kmp.mvi.account.AccountField
import com.teddyfreddy.kmp.mvi.account.AccountStore
import kotlinx.datetime.LocalDate

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
        @Suppress("unused")
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
        data class ChangeField(val field: AccountField, val value: Any?, val validate: Boolean = false) :
            Event
        data class ValidateField(val field: AccountField, val forceValid: Boolean? = false) : Event
        object Cancel : Event
        object Continue : Event
    }

    fun changeEmail(newVal: String)
    fun focusChangeEmail(focused: Boolean)
    fun changePassword(newVal: String)
    fun focusChangePassword(focused: Boolean)
    fun changePasswordConfirmation(newVal: String)
    fun focusChangePasswordConfirmation(focused: Boolean)
    fun changeGivenName(newVal: String)
    fun focusChangeGivenName(focused: Boolean)
    fun changeFamilyName(newVal: String)
    fun focusChangeFamilyName(focused: Boolean)
    fun changePhoneNumber(newVal: String)

    fun cancelPressed()
    fun continuePressed()

    val onLabel: suspend (label: AccountStore.Label) -> Unit
    fun onContinue()
    fun onCancel()
}


// Added for label delegation and Compose aggregation
@Suppress("unused")
open class AccountBaseMviView :
    BaseMviView<AccountMviView.Model, AccountMviView.Event>(), AccountMviView {

    private var model: AccountMviView.Model = AccountMviView.Model()
    override fun render(model: AccountMviView.Model) {
        this.model = model
    }

    override val onLabel: suspend (label: AccountStore.Label) -> Unit = { // Invoked via bindings in binder
        when (it) {
            AccountStore.Label.Continue -> this.onContinue()
            AccountStore.Label.Cancel -> this.onCancel()
        }
    }

    override fun onContinue() {}
    override fun onCancel() {}

    @Suppress("unused")
    override fun changeEmail(newVal: String) {
        dispatch(AccountMviView.Event.ChangeField(AccountField.Email, newVal, false))
    }
    @Suppress("unused")
    override fun focusChangeEmail(focused: Boolean) {
        dispatch(AccountMviView.Event.ValidateField(AccountField.Email, focused && model.email.data.isEmpty()))
    }
    @Suppress("unused")
    override fun changePassword(newVal: String) {
        dispatch(AccountMviView.Event.ChangeField(AccountField.Password, newVal, false))
    }
    @Suppress("unused")
    override fun focusChangePassword(focused: Boolean) {
        dispatch(AccountMviView.Event.ValidateField(AccountField.Password, focused && model.password.data.isEmpty()))
    }
    @Suppress("unused")
    override fun changePasswordConfirmation(newVal: String) {
        dispatch(AccountMviView.Event.ChangeField(AccountField.PasswordConfirmation, newVal, false))
    }
    @Suppress("unused")
    override fun focusChangePasswordConfirmation(focused: Boolean) {
        dispatch(AccountMviView.Event.ValidateField(AccountField.PasswordConfirmation, focused && model.passwordConfirmation.data.isEmpty()))
    }
    @Suppress("unused")
    override fun changeGivenName(newVal: String) {
        dispatch(AccountMviView.Event.ChangeField(AccountField.FirstName, newVal, false))
    }
    @Suppress("unused")
    override fun focusChangeGivenName(focused: Boolean) {
        dispatch(AccountMviView.Event.ValidateField(AccountField.FirstName, focused && model.givenName.data.isEmpty()))
    }
    @Suppress("unused")
    override fun changeFamilyName(newVal: String) {
        dispatch(AccountMviView.Event.ChangeField(AccountField.LastName, newVal, false))
    }
    @Suppress("unused")
    override fun focusChangeFamilyName(focused: Boolean) {
        dispatch(AccountMviView.Event.ValidateField(AccountField.LastName, focused && model.familyName.data.isEmpty()))
    }
    @Suppress("unused")
    override fun changePhoneNumber(newVal: String) {
        dispatch(AccountMviView.Event.ChangeField(AccountField.PhoneNumber, newVal, false))
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



