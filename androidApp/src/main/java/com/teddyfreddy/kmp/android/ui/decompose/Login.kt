package com.teddyfreddy.kmp.android.ui.decompose

import com.arkivanov.decompose.value.Value

interface Login {
    fun login(username: String, password: String)
    fun signup()

    data class Model(
        var username: String = "",
        var password: String = "",
        var isUsernameError: Boolean = false,
        var isPasswordError: Boolean = false
    ) {
        constructor() : this(
            username = "admin",
            password = "pw",
            isUsernameError = false,
            isPasswordError = false
        )
    }

    val model: Value<Model>

    fun onUsernameChange(newval: String)
    fun onPasswordChange(newval: String)
}