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
import com.teddyfreddy.kmp.android.ui.extensions.PasswordTextField
import com.teddyfreddy.kmp.android.ui.extensions.UsernameTextField

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginView(
    onLogin: (String, String) -> Unit,
    onSignup: () -> Unit,
    modifier: Modifier? = Modifier
) {
    var username: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }
    var isUsernameError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

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
            value = username,
            onValueChange = {
                username = it
                isUsernameError = username.isEmpty()
            },
            modifier = Modifier
                .onFocusChanged {
                    if (it.hasFocus && !username.isEmpty()) {
                        isUsernameError = false
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
            isError = isUsernameError,
            onNext = {
                if (username.isEmpty()) isUsernameError = true
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordTextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordError = password.isEmpty()
            },
            modifier = Modifier
                .onFocusChanged {
                    if (it.hasFocus && !password.isEmpty()) {
                        isPasswordError = false
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
            isError = isPasswordError,
            errorText = "Your password isn't correct",
            onGo = { if (password.isEmpty()) isPasswordError = true else onLogin(username, password) }
        )
        Spacer(modifier = Modifier.padding(20.dp))
        Text("Don't have an account?")
        Button(onClick = { onSignup() }) {
            Text("Sign up")
        }
    }
}