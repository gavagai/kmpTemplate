package com.teddyfreddy.kmp.android.ui.compose.login

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.teddyfreddy.android.ui.extensions.*
import com.teddyfreddy.kmp.android.ui.decompose.login.Login
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    component: Login,
    modifier: Modifier? = Modifier
) {
    val state = remember { component.state }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    fun showSnackbar(message: String) {
         scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    fun doLogin() {
        component.login { snackbarMessage ->
            showSnackbar(snackbarMessage ?: "")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier?.fillMaxWidth() ?: Modifier.fillMaxWidth()
        ) {
            val focusManager = LocalFocusManager.current

            Spacer(modifier = Modifier.padding(20.dp))
            Text("Shall We")
            Spacer(modifier = Modifier.padding(4.dp))
            Row {
                Icon(Icons.Filled.Person, "logo")
                Icon(Icons.Filled.Person, "logo")
            }

            Spacer(modifier = Modifier.padding(20.dp))
            UsernameTextField(
                value = state.value.username.data,
                onValueChange = {
                    component.changeUsername(it)
                },
                modifier = Modifier.standardKeyNavigation(focusManager, up = false),
                placeholder = { Text("Username (or email)*") },
                isError = state.value.username.error != null,
                errorText = state.value.username.error,
                required = true,
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                },
                onFocusChange = { focused ->
                    component.focusChangeUsername(focused)
                }
            )

            Spacer(modifier = Modifier.padding(4.dp))
            PasswordTextField(
                value = state.value.password.data,
                onValueChange = {
                    component.changePassword(it)
                },
                modifier = Modifier.standardKeyNavigation(
                    focusManager,
                    down = true,
                    enterMeansDown = state.value.emailVerificationRequired
                ),
                trailingIcon = if (state.value.emailVerificationRequired) { {} } else null,
                isError = state.value.password.error != null,
                errorText = state.value.password.error,
                required = true,
                onGo = if (!state.value.emailVerificationRequired) {
                    { doLogin() }
                } else null,
                onFocusChange = { focused ->
                    component.focusChangePassword(focused)
                }
            )

            if (state.value.emailVerificationRequired) {
                Spacer(modifier = Modifier.padding(4.dp))
                OneTimeCodeTextField(
                    value = state.value.verificationCode.data,
                    onValueChange = {
                        component.changeVerificationCode(it)
                    },
                    modifier = Modifier.standardKeyNavigation(
                        focusManager,
                        down = false,
                        enterMeansDown = false
                    ),
                    isError = state.value.verificationCode.error != null,
                    required = true,
                    supportingText = "Your code was sent to you when you signed up",
                    errorText = state.value.verificationCode.error,
                    onGo = {
                        doLogin()
                    },
                    onFocusChange = { focused ->
                        component.focusChangeVerificationCode(focused)
                    }
                )

                Spacer(modifier = Modifier.padding(10.dp))
                Text("Need a new verification code?")
                Button(
                    onClick = {
                        component.getNewCode { message ->
                            showSnackbar(message)
                        }
                    }
                ) {
                    Text("Get code")
                }
                Spacer(modifier = Modifier.padding(20.dp))
            }
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