package com.teddyfreddy.kmp.viewmodel

import com.arkivanov.mvikotlin.core.binder.Binder
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.MviView
import com.arkivanov.mvikotlin.extensions.coroutines.*
import com.teddyfreddy.kmp.ValidatedStringField
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate



data class RegistrationContext(
    var email: String? = null,
    var givenName: String? = null,
    var familyName: String? = null
)



class AccountMVIViewProxy(
    private val onRender: (AccountMVIView.Model) -> Unit,
    onForward: () -> Unit,
    onCancel: () -> Unit
) : BaseMviView<AccountMVIView.Model, AccountMVIView.Event>(), AccountMVIView {
    override fun render(model: AccountMVIView.Model) { // Invoked via bindings in binder
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
interface AccountMVIView : MviView<AccountMVIView.Model, AccountMVIView.Event> {

    data class Model(
        var email: ValidatedStringField = ValidatedStringField(data = ""),
        var password: ValidatedStringField = ValidatedStringField(data = ""),
        var passwordConfirmation: ValidatedStringField = ValidatedStringField(data = ""),
        var givenName: ValidatedStringField = ValidatedStringField(data = ""),
        var familyName: ValidatedStringField = ValidatedStringField(data = ""),
        var phone: String = "",
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
typealias AccountMVIViewModel = AccountMVIView.Model // For iOS visibility


internal val stateToModel: AccountStore.State.() -> AccountMVIView.Model =
    {
        AccountMVIView.Model(
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

internal val eventToIntent: AccountMVIView.Event.() -> AccountStore.Intent =
    {
        when (this) {
            is AccountMVIView.Event.ChangeField -> AccountStore.Intent.ChangeField(field, value, validate)
            is AccountMVIView.Event.ValidateField -> AccountStore.Intent.ValidateField(field)
            AccountMVIView.Event.Cancel -> AccountStore.Intent.Cancel
            AccountMVIView.Event.Forward -> AccountStore.Intent.Continue
        }
    }

class AccountBinder(private val store: AccountStore) {
    private var binder: Binder? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onViewCreated(view: AccountMVIViewProxy) {
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
