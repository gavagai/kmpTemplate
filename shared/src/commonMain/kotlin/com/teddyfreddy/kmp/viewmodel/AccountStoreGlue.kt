package com.teddyfreddy.kmp.viewmodel

import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.extensions.coroutines.*
import com.teddyfreddy.kmp.ValidatedField
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate



data class RegistrationContext(
    var email: String? = null,
    var givenName: String? = null,
    var familyName: String? = null
)



class AccountViewProxy(
    private val onRender: (AccountView.Model) -> Unit,
    onForward: () -> Unit,
    onCancel: () -> Unit
) : BaseMviView<AccountView.Model, AccountView.Event>(), AccountView {
    override fun render(model: AccountView.Model) { // Invoked via bindings in binder
        onRender(model)
    }

    internal val onLabel: suspend (label: AccountStore.Label) -> Unit = { // Invoked via bindings in binder
        when (it) {
            AccountStore.Label.Continue -> onForward()
            AccountStore.Label.Cancel -> onCancel()
        }
    }
}


/**
 * Interface for proxy
 */
interface AccountView : MviView<AccountView.Model, AccountView.Event> {

    data class Model(
        var email: ValidatedField = ValidatedField(value = ""),
        var password: ValidatedField = ValidatedField(value = ""),
        var passwordConfirmation: ValidatedField = ValidatedField(value = ""),
        var givenName: ValidatedField = ValidatedField(value = ""),
        var familyName: ValidatedField = ValidatedField(value = ""),
        var phone: String? = "",
        var dateOfBirth: LocalDate? = null,

        var optionalsShown: Boolean = false,
    )

    sealed interface Event {
        data class ChangeField(val field: AccountViewField, val value: Any?, val validate: Boolean = false) : Event
        data class ValidateField(val field: AccountViewField) : Event
        object Cancel : Event
        object Forward : Event
    }
}


internal val stateToModel: AccountStore.State.() -> AccountView.Model =
    {
        AccountView.Model(
            email = email,
            password = password,
            passwordConfirmation = passwordConfirmation,
            givenName = givenName,
            familyName = familyName,
            phone = phone,
            dateOfBirth = dateOfBirth,

            optionalsShown = optionalsShown
        )
    }

internal val eventToIntent: AccountView.Event.() -> AccountStore.Intent =
    {
        when (this) {
            is AccountView.Event.ChangeField -> AccountStore.Intent.ChangeField(field, value, validate)
            is AccountView.Event.ValidateField -> AccountStore.Intent.ValidateField(field)
            AccountView.Event.Cancel -> AccountStore.Intent.Cancel
            AccountView.Event.Forward -> AccountStore.Intent.Continue
        }
    }

class AccountBinder(private val store: AccountStore) {
    private var binder: Binder? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onViewCreated(view: AccountViewProxy) {
        binder = bind {
            store.states.map(stateToModel).distinctUntilChanged() bindTo view
            store.labels bindTo view.onLabel
            view.events.map(eventToIntent).distinctUntilChanged() bindTo store
        }
    }

    fun onStart() {
        binder?.start()
    }

    fun onStop() {
        binder?.stop()
    }

    fun onViewDestroyed() {
        binder = null
    }

    fun onDestroy() {
        store.dispose()
    }
}
