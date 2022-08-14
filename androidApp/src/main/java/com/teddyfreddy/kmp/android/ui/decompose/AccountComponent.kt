package com.teddyfreddy.kmp.android.ui.decompose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.teddyfreddy.kmp.account.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalCoroutinesApi::class)
class AccountComponent(
    componentContext: ComponentContext,
    private val onContinue: () -> Unit,
    private val onCancel: () -> Unit
) : Account, ComponentContext by componentContext {

    private val store = AccountStoreFactory(DefaultStoreFactory(), RegistrationContext()).create()

    private var _state: MutableState<AccountStore.State> = mutableStateOf(store.state)
    override val state = _state


    private val scope = CoroutineScope(Dispatchers.Main)
    init {
        scope.launch {
            store.states.distinctUntilChanged().collect {
                this@AccountComponent._state.value = it
            }
        }
        scope.launch {
            store.labels.collect {
                when (it) {
                    is AccountStore.Label.Continue -> this@AccountComponent.onContinue()
                    is AccountStore.Label.Cancel -> this@AccountComponent.onCancel()
                }
            }
        }
        lifecycle.doOnDestroy { scope.cancel() }
    }

    override fun changeField(field: AccountField, value: Any?, validate: Boolean) {
        store.accept(AccountStore.Intent.ChangeField(field, value, validate))
    }

    override fun validateField(field: AccountField) {
        store.accept(AccountStore.Intent.ValidateField(field))
    }

    override fun cancelPressed() {
        store.accept(AccountStore.Intent.Cancel)
    }

    override fun continuePressed() {
        store.accept(AccountStore.Intent.Continue)
    }

}
