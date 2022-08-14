package com.teddyfreddy.kmp.android.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.teddyfreddy.kmp.android.ui.decompose.Login
import com.teddyfreddy.kmp.android.ui.extensions.PasswordTextField
import com.teddyfreddy.kmp.android.ui.extensions.UsernameTextField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginView(
    component: Login,
    modifier: Modifier? = Modifier
) {
    val state by component.model.subscribeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier ?: Modifier
    ) {
        val focusManager = LocalFocusManager.current

        Spacer(modifier = Modifier.padding(20.dp))
        Text("Team Share")
        Spacer(modifier = Modifier.padding(4.dp))
        Row {
            Icon(Icons.Filled.Person, "logo")
            Icon(Icons.Filled.Person, "logo")
            Icon(Icons.Filled.Person, "logo")
        }

        Spacer(modifier = Modifier.padding(20.dp))
        UsernameTextField(
            value = state.username,
            onValueChange = {
                component.onUsernameChange(it)
                state.isUsernameError = state.username.isEmpty()
            },
            modifier = Modifier
                .onFocusChanged {
                    if (it.hasFocus && !state.username.isEmpty()) {
                        state.isUsernameError = false
                    }
                }
                .onPreviewKeyEvent {
                    if (it.key == Key.Tab && !it.isShiftPressed && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                        focusManager.moveFocus(FocusDirection.Down)
                        true
                    } else if (it.key == Key.Enter && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                        focusManager.moveFocus(FocusDirection.Down)
                        true
                    } else {
                        false
                    }
                },
            isError = state.isUsernameError,
            onNext = {
                if (state.username.isEmpty()) state.isUsernameError = true
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordTextField(
            value = state.password,
            onValueChange = {
                component.onPasswordChange(it)
                state.isPasswordError = state.password.isEmpty()
            },
            modifier = Modifier
                .onFocusChanged {
                    if (it.hasFocus && !state.password.isEmpty()) {
                        state.isPasswordError = false
                    }
                }
                .onPreviewKeyEvent {
                    if (it.key == Key.Tab && it.isShiftPressed && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                        focusManager.moveFocus(FocusDirection.Up)
                        true
                    }
                    else {
                        false
                    }
                },
            isError = state.isPasswordError,
            errorText = "Your password isn't correct",
            onGo = {
                if (state.password.isEmpty()) state.isPasswordError = true
                else component.login(state.username, state.password)
            }
        )
        Spacer(modifier = Modifier.padding(20.dp))
        Text("Don't have an account?")
        Button(
            onClick = { component.signup() }
        ) {
            Text("Sign up")
        }
    }
}