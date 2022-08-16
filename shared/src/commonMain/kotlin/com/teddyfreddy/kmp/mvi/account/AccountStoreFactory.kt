package com.teddyfreddy.kmp.mvi.account

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.teddyfreddy.common.Field
import com.teddyfreddy.kmp.mvi.RegistrationContext
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class AccountStoreFactory(
    private val storeFactory: StoreFactory,

    // Parameters for bootstrapper
    private val registrationContext: RegistrationContext
) {
    private sealed interface Msg {
        data class ChangeField(val field: AccountField, val value: Any?) : Msg
        data class ValidateField(val field: AccountField, val forceValid: Boolean? = false) : Msg
    }

    private sealed interface Action {
        data class RestoreFromRegistrationContext(val registrationContext: RegistrationContext):
            Action
    }

    fun create(): AccountStore =
        object : AccountStore,
            Store<AccountStore.Intent, AccountStore.State, AccountStore.Label> by storeFactory.create(
                name = "AccountStore",
                initialState = AccountStore.State(),
                bootstrapper = BootstrapperImpl(registrationContext),
                executorFactory = AccountStoreFactory::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private object ReducerImpl : Reducer<AccountStore.State, Msg> {
        override fun AccountStore.State.reduce(msg: Msg): AccountStore.State =
            when (msg) {
                is Msg.ChangeField -> {
                    when (msg.field) {
                        AccountField.Username -> {
                            val email = email.copy(
                                data = msg.value as? String ?: ""
                            )
                            copy(
                                email = email
                            )
                        }
                        AccountField.Password -> {
                            copy(
                                password = password.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        AccountField.PasswordConfirmation -> {
                            copy(
                                passwordConfirmation = passwordConfirmation.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        AccountField.FirstName -> {
                            copy(
                                givenName = givenName.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        AccountField.LastName -> {
                            copy(
                                familyName = familyName.copy(
                                    data = msg.value as? String ?: ""
                                )
                            )
                        }
                        AccountField.PhoneNumber -> {
                            copy(
                                phone = msg.value as? String ?: ""
                            )
                        }
                        AccountField.DateOfBirth -> {
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
                    val forceValid = msg.forceValid != null && msg.forceValid
                    when (msg.field) {
                        AccountField.Username -> {
                            copy(
                                email = email.copy(
                                    error = if (forceValid) null else validator.validate(msg.field, email.data)
                                )
                            )
                        }
                        AccountField.Password -> {
                            copy(
                                password = password.copy(
                                    error = if (forceValid) null else validator.validate(msg.field, password.data)
                                )
                            )
                        }
                        AccountField.PasswordConfirmation -> {
                            copy(
                                passwordConfirmation = passwordConfirmation.copy(
                                    error = if (forceValid) null else validator.validate(msg.field, passwordConfirmation.data, password.data)
                                )
                            )
                        }
                        AccountField.FirstName -> {
                            copy(
                                givenName = givenName.copy(
                                    error = if (forceValid) null else validator.validate(msg.field, givenName.data)
                                )
                            )
                        }
                        AccountField.LastName -> {
                            copy(
                                familyName = familyName.copy(
                                    error = if (forceValid) null else validator.validate(msg.field, familyName.data)
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
                is AccountStore.Intent.ValidateField -> dispatch(Msg.ValidateField(intent.field, intent.forceValid))
            }

        private fun executeContinue(getState: () -> AccountStore.State) {
            dispatch(Msg.ValidateField(AccountField.Username))
            dispatch(Msg.ValidateField(AccountField.Password))
            dispatch(Msg.ValidateField(AccountField.PasswordConfirmation))
            dispatch(Msg.ValidateField(AccountField.FirstName))
            dispatch(Msg.ValidateField(AccountField.LastName))
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
            dispatch(Msg.ChangeField(AccountField.Username, registrationContext.email))
            dispatch(Msg.ChangeField(AccountField.FirstName, registrationContext.givenName))
            dispatch(Msg.ChangeField(AccountField.LastName, registrationContext.familyName))
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