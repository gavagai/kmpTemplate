package com.teddyfreddy.kmp.viewmodel.account

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.teddyfreddy.kmp.mvi.RegistrationContext
import com.teddyfreddy.kmp.mvi.account.AccountStore
import com.teddyfreddy.kmp.mvi.account.AccountStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Suppress("unused")
open class AccountController(lifecycle: Lifecycle, registrationContext: RegistrationContext) {
    private var store: AccountStore = AccountStoreFactory(DefaultStoreFactory(), registrationContext).create()

    init {
        lifecycle.doOnDestroy(store::dispose)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("unused")
    fun onViewCreated(view: AccountMviView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.START_STOP, Dispatchers.Unconfined) {
            store.states.map(stateToModel).distinctUntilChanged() bindTo view
            store.labels bindTo view.onLabel
            view.events.map(eventToIntent) bindTo store
        }
    }
}
