package com.teddyfreddy.kmp.account

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

open class AccountController(lifecycle: Lifecycle, registrationContext: RegistrationContext) {
    private lateinit var store: AccountStore

    init {
        runBlocking { // Not sure about this. Bootstrapper needs a coroutine context for launch.
            store = AccountStoreFactory(DefaultStoreFactory(), registrationContext).create()
        }
        lifecycle.doOnDestroy(store::dispose)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun onViewCreated(view: AccountMviView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.START_STOP, Dispatchers.Unconfined) {
            store.states.map(stateToModel).distinctUntilChanged() bindTo view
            store.labels bindTo view.onLabel
            view.events.map(eventToIntent) bindTo store
        }
    }
}
