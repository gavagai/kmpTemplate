package com.teddyfreddy.kmp.viewmodel

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.teddyfreddy.kmp.ValidatedStringField
import com.teddyfreddy.kmp.emailValidator
import com.teddyfreddy.kmp.stringValidator
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate


enum class AccountViewField(
    override val label: String,
    override val required: Boolean = false,
    override val validator: Field.Validator? = null
) : Field {
    Username("Email", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            emailValidator(field.label, value as? String, field.required)
        }
    ),
    Password("Password", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required)
        }
    ),
    PasswordConfirmation("Password confirmation", true,
        validator = Field.Validator { field: Field, value: Any?, args: Array<out Any?> ->
            var error = stringValidator(field.label, value as? String, field.required)
            if (error == null) {
                if (value != args[0]) {
                    error = "Passwords don't match"
                }
            }
            error
        }
    ),
    FirstName("First name", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required)
        }
    ),
    LastName("Last name", true,
        validator = Field.Validator { field: Field, value: Any?, _ ->
            stringValidator(field.label, value as? String, field.required)
        }
    ),
    PhoneNumber("Phone number"),
    DateOfBirth("Date of birth")
}


interface AccountStore : Store<AccountStore.Intent, AccountStore.State, AccountStore.Label> {

    sealed interface Intent {
        data class ChangeField(val field: AccountViewField, val value: Any?, val validate: Boolean = false) : Intent
        data class ValidateField(val field: AccountViewField) : Intent
        object Cancel : Intent
        object Continue : Intent
    }

    data class State(
        var email: ValidatedStringField = ValidatedStringField(data = ""),
        var password: ValidatedStringField = ValidatedStringField(data = ""),
        var passwordConfirmation: ValidatedStringField = ValidatedStringField(data = ""),
        var givenName: ValidatedStringField = ValidatedStringField(data = ""),
        var familyName: ValidatedStringField = ValidatedStringField(data = ""),
        var phone: String = "",
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
                        AccountViewField.Username -> {
                            val email = email.copy(
                                data = msg.value as? String ?: ""
                            )
                            copy(
                                email = email
                            )
                        }
                        AccountViewField.Password -> {
                            copy(
                                password = password.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        AccountViewField.PasswordConfirmation -> {
                            copy(
                                passwordConfirmation = passwordConfirmation.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        AccountViewField.FirstName -> {
                            copy(
                                givenName = givenName.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        AccountViewField.LastName -> {
                            copy(
                                familyName = familyName.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        AccountViewField.PhoneNumber -> {
                            copy(
                                phone = msg.value as? String ?: ""
                            )
                        }
                        AccountViewField.DateOfBirth -> {
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
                        AccountViewField.Username -> {
                            copy(
                                email = email.copy(
                                    error = validator.validate(msg.field, email.data)
                                )
                            )
                        }
                        AccountViewField.Password -> {
                            copy(
                                password = password.copy(
                                    error = validator.validate(msg.field, password.data)
                                )
                            )
                        }
                        AccountViewField.PasswordConfirmation -> {
                            copy(
                                passwordConfirmation = passwordConfirmation.copy(
                                    error = validator.validate(msg.field, passwordConfirmation.data, password.data)
                                )
                            )
                        }
                        AccountViewField.FirstName -> {
                            copy(
                                givenName = givenName.copy(
                                    error = validator.validate(msg.field, givenName.data)
                                )
                            )
                        }
                        AccountViewField.LastName -> {
                            copy(
                                familyName = familyName.copy(
                                    error = validator.validate(msg.field, familyName.data)
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
            dispatch(Msg.ValidateField(AccountViewField.Username))
            dispatch(Msg.ValidateField(AccountViewField.Password))
            dispatch(Msg.ValidateField(AccountViewField.PasswordConfirmation))
            dispatch(Msg.ValidateField(AccountViewField.FirstName))
            dispatch(Msg.ValidateField(AccountViewField.LastName))
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
            dispatch(Msg.ChangeField(AccountViewField.Username, registrationContext.email))
            dispatch(Msg.ChangeField(AccountViewField.FirstName, registrationContext.givenName))
            dispatch(Msg.ChangeField(AccountViewField.LastName, registrationContext.familyName))
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