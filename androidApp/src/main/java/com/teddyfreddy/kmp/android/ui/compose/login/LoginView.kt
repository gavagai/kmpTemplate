package com.teddyfreddy.kmp.android.ui.compose.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.teddyfreddy.kmp.android.ui.decompose.Login
import com.teddyfreddy.kmp.android.ui.extensions.PasswordTextField
import com.teddyfreddy.kmp.android.ui.extensions.UsernameTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    component: Login,
    modifier: Modifier? = Modifier
) {
    val state = remember { component.state }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var loginErrorMessage: MutableState<String?> = mutableStateOf(null)

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = {
                    Snackbar(snackbarData =
                        object : SnackbarData {
                            override val visuals: SnackbarVisuals
                                get() = object : SnackbarVisuals {
                                    override val actionLabel: String? = null
                                    override val duration: SnackbarDuration = SnackbarDuration.Short
                                    override val message: String = loginErrorMessage.value ?: ""
                                    override val withDismissAction: Boolean = false
                                }
                            override fun dismiss() {}
                            override fun performAction() {}
                        }
                    )
                }
            )
        }
    ) {
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
                    focusManager.moveFocus(FocusDirection.Down)
                },
                onValidate = { forceValid ->
                    component.validateUsername(forceValid)
                }
            )
            Spacer(modifier = Modifier.padding(4.dp))
            PasswordTextField(
                value = state.value.password.data,
                onValueChange = {
                    component.changePassword(it)
                },
                modifier = Modifier
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
                    component.login { message ->
                        if (message != null) {
                            loginErrorMessage.value = message
                            scope.launch {
                                snackbarHostState.showSnackbar(object : SnackbarVisuals {
                                        override val actionLabel: String? = null
                                        override val duration: SnackbarDuration = SnackbarDuration.Short
                                        override val message: String = loginErrorMessage.value ?: ""
                                        override val withDismissAction: Boolean = false
                                    }
                                )
                            }
                        }
                    }
                },
                onValidate = { forceValid ->
                    component.validatePassword(forceValid)
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
}