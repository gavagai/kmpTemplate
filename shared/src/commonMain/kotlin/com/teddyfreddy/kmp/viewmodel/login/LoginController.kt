package com.teddyfreddy.kmp.viewmodel.login

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.teddyfreddy.kmp.mvi.login.LoginStore
import com.teddyfreddy.kmp.mvi.login.LoginStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@Suppress("unused")
open class LoginController(lifecycle: Lifecycle) {
    private lateinit var store: LoginStore

    init {
        runBlocking { // Bootstrapper needs a coroutine context for launch in SwiftUI.
            store = LoginStoreFactory(DefaultStoreFactory(), null/* TODO */).create()
        }
        lifecycle.doOnDestroy(store::dispose)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Suppress("unused")
    fun onViewCreated(view: LoginMviView, viewLifecycle: Lifecycle) {
        bind(viewLifecycle, BinderLifecycleMode.START_STOP, Dispatchers.Unconfined) {
            store.states.map(stateToModel).distinctUntilChanged() bindTo view
            store.labels bindTo view.onLabel
            view.events.map(eventToIntent) bindTo store
        }
    }
}