package com.teddyfreddy.kmp.viewmodel

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.teddyfreddy.kmp.ValidatedField
import com.teddyfreddy.kmp.emailValidator
import com.teddyfreddy.kmp.stringValidator
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate


sealed class AccountViewField(
    override val name: String,
    override val required: Boolean = false,
    override val validator: Field.Validator? = null
) : Field {
    object UsernameField : AccountViewField("Email", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            emailValidator(field.name, value as? String, field.required)
        }
    )
    object PasswordField : AccountViewField("Password", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.name, value as? String, field.required)
        }
    )
    object PasswordConfirmationField : AccountViewField("Password confirmation", true,
        validator = Field.Validator { field: Field, value: Any?, args: Array<out Any?>->
            var error = stringValidator(field.name, value as? String, field.required)
            if (error == null) {
                if (value != args[0]) {
                    error = "Passwords don't match"
                }
            }
            error
        }
    )
    object FirstNameField : AccountViewField("First name", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.name, value as? String, field.required)
        }
    )
    object LastNameField : AccountViewField("Last name", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.name, value as? String, field.required)
        }
    )
    object PhoneNumberField : AccountViewField("Phone number")
    object DateOfBirthField : AccountViewField("Date of birth")
}


interface AccountStore : Store<AccountStore.Intent, AccountStore.State, AccountStore.Label> {

    sealed interface Intent {
        data class ChangeField(val field: AccountViewField, val value: Any?, val validate: Boolean = false) : Intent
        data class ValidateField(val field: AccountViewField) : Intent
        object Cancel : Intent
        object Continue : Intent
    }

    data class State(
        var email: ValidatedField = ValidatedField(value = ""),
        var password: ValidatedField = ValidatedField(value = ""),
        var passwordConfirmation: ValidatedField = ValidatedField(value = ""),
        var givenName: ValidatedField = ValidatedField(value = ""),
        var familyName: ValidatedField = ValidatedField(value = ""),
        var phone: String? = "",
        var dateOfBirth: LocalDate? = null,

        var optionalsShown: Boolean = false,

        val valid: Boolean =
                    email.error == null &&
                    password.error == null &&
                    passwordConfirmation.error == null &&
                    givenName.error == null &&
                    familyName.error == null
    )

    sealed interface Label {
        object Cancel: Label
        object Continue: Label
    }
}

class AccountStoreProvider(
    private val storeFactory: StoreFactory,

    // Parameters for bootstrapper
    private val registrationContext: RegistrationContext
) {
    private sealed interface Msg {
        data class ChangeField(val field: AccountViewField, val value: Any?) : Msg
        data class ValidateField(val field: AccountViewField) : Msg
    }

    private sealed interface Action {
        data class RestoreFromRegistrationContext(val registrationContext: RegistrationContext): Action
    }

    fun provide(): AccountStore =
        object : AccountStore,
            Store<AccountStore.Intent, AccountStore.State, AccountStore.Label> by storeFactory.create(
                name = "AccountStore",
                initialState = AccountStore.State(),
                bootstrapper = BootstrapperImpl(registrationContext),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private object ReducerImpl : Reducer<AccountStore.State, Msg> {
        override fun AccountStore.State.reduce(msg: Msg): AccountStore.State =
            when (msg) {
                is Msg.ChangeField -> {
                    when (msg.field) {
                        AccountViewField.UsernameField -> {
                            val email = email.copy(
                                value = msg.value as? String
                            )
                            copy(
                                email = email
                            )
                        }
                        AccountViewField.PasswordField -> {
                            copy(
                                password = password.copy(
                                    value = msg.value as? String
                                )
                            )
                        }
                        AccountViewField.PasswordConfirmationField -> {
                            copy(
                                passwordConfirmation = passwordConfirmation.copy(
                                    value = msg.value as? String
                                )
                            )
                        }
                        AccountViewField.FirstNameField -> {
                            copy(
                                givenName = givenName.copy(
                                    value = msg.value as? String
                                )
                            )
                        }
                        AccountViewField.LastNameField -> {
                            copy(
                                familyName = familyName.copy(
                                    value = msg.value as? String
                                )
                            )
                        }
                        AccountViewField.PhoneNumberField -> {
                            copy(
                                phone = msg.value as? String
                            )
                        }
                        AccountViewField.DateOfBirthField -> {
                            copy(
                                dateOfBirth = msg.value as? LocalDate
                            )
                        }
                    }
                }
                is Msg.ValidateField -> {
                    val validator = msg.field.validator ?: Field.Validator { _, _, _ ->
                        null
                    }
                    when (msg.field) {
                        AccountViewField.UsernameField -> {
                            copy(
                                email = email.copy(
                                    error = validator.validate(msg.field, email.value)
                                )
                            )
                        }
                        AccountViewField.PasswordField -> {
                            copy(
                                password = password.copy(
                                    error = validator.validate(msg.field, password.value)
                                )
                            )
                        }
                        AccountViewField.PasswordConfirmationField -> {
                            copy(
                                passwordConfirmation = passwordConfirmation.copy(
                                    error = validator.validate(msg.field, passwordConfirmation.value, password.value)
                                )
                            )
                        }
                        AccountViewField.FirstNameField -> {
                            copy(
                                givenName = givenName.copy(
                                    error = validator.validate(msg.field, givenName.value)
                                )
                            )
                        }
                        AccountViewField.LastNameField -> {
                            copy(
                                familyName = familyName.copy(
                                    error = validator.validate(msg.field,familyName.value)
                                )
                            )
                        }
                        else -> this
                    }
                }
            }
    }

    private class ExecutorImpl : CoroutineExecutor<AccountStore.Intent, Action, AccountStore.State, Msg, AccountStore.Label>() {
        override fun executeIntent(intent: AccountStore.Intent, getState: () -> AccountStore.State) =
            when (intent) {
                is AccountStore.Intent.Cancel -> publish(AccountStore.Label.Cancel)
                is AccountStore.Intent.Continue -> executeContinue(getState)
                is AccountStore.Intent.ChangeField -> executeChangeField(intent)
                is AccountStore.Intent.ValidateField -> dispatch(Msg.ValidateField(intent.field))
            }

        private fun executeContinue(getState: () -> AccountStore.State) {
            dispatch(Msg.ValidateField(AccountViewField.UsernameField))
            dispatch(Msg.ValidateField(AccountViewField.PasswordField))
            dispatch(Msg.ValidateField(AccountViewField.PasswordConfirmationField))
            dispatch(Msg.ValidateField(AccountViewField.FirstNameField))
            dispatch(Msg.ValidateField(AccountViewField.LastNameField))
            if (getState().valid) {
                publish(AccountStore.Label.Continue)
            }
        }

        private fun executeChangeField(intent: AccountStore.Intent.ChangeField) {
            dispatch(Msg.ChangeField(intent.field, intent.value))
            if (intent.validate) {
                dispatch(Msg.ValidateField(intent.field))
            }
        }

        override fun executeAction(action: Action, getState: () -> AccountStore.State) {
            when (action) {
                is Action.RestoreFromRegistrationContext -> executeRestoreFromRegistrationContext(action.registrationContext)
            }
        }

        private fun executeRestoreFromRegistrationContext(registrationContext: RegistrationContext) {
            dispatch(Msg.ChangeField(AccountViewField.UsernameField, registrationContext.email))
            dispatch(Msg.ChangeField(AccountViewField.FirstNameField, registrationContext.givenName))
            dispatch(Msg.ChangeField(AccountViewField.LastNameField, registrationContext.familyName))
        }
    }

    private class BootstrapperImpl(private val registrationContext: RegistrationContext) : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                dispatch(Action.RestoreFromRegistrationContext(registrationContext))
            }
        }
    }
}