package com.teddyfreddy.kmp.android.ui.login

import androidx.compose.foundation.layout.*
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
import com.teddyfreddy.kmp.android.ui.decompose.Login
import com.teddyfreddy.kmp.android.ui.extensions.PasswordTextField
import com.teddyfreddy.kmp.android.ui.extensions.UsernameTextField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginView(
    component: Login,
    modifier: Modifier? = Modifier
) {
    val state = remember { component.state }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier?.fillMaxWidth() ?: Modifier.fillMaxWidth()
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
            value = state.value.username.data,
            onValueChange = {
                component.changeUsername(it)
            },
            modifier = Modifier
                .onFocusChanged {
                    if (it.isFocused && !state.value.username.data.isEmpty()) {
                        component.validateUsername()
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
            isError = state.value.username.error != null,
            errorText = state.value.username.error,
            onNext = {
                component.validateUsername()
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordTextField(
            value = state.value.password.data,
            onValueChange = {
                component.changePassword(it)
            },
            modifier = Modifier
                .onFocusChanged {
                    if (it.isFocused && !state.value.password.data.isEmpty()) {
                        component.validatePassword()
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
            isError = state.value.password.error != null,
            errorText = state.value.password.error,
            onGo = {
                 component.login()
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