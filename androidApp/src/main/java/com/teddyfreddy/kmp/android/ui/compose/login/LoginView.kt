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
import com.teddyfreddy.android.ui.extensions.*
import com.teddyfreddy.kmp.android.ui.decompose.Login
import com.teddyfreddy.common.network.NetworkRequestError
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

    fun showSnackbar(message: String) {
         scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    fun doLogin() {
        component.login { exception ->
            if (exception != null) {
                  val snackbarMessage = when (exception) {
                    is NetworkRequestError -> {
                        when (exception) {
                            NetworkRequestError.EmailVerificationFailed -> {
                                component.setEmailVerificationCodeError(exception.failureReason!!)
                            }
                            NetworkRequestError.EmailVerificationCodeExpired -> {
                                component.setEmailVerificationCodeError(exception.failureReason!!)
                            }
                            else -> {}
                        }
                        "${exception.failureReason!!}${if (exception.recoverySuggestion != null) " - ${exception.recoverySuggestion!!}" else ""}"
                    }
                    else -> exception.message
                }
                showSnackbar(snackbarMessage ?: "")
            }
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
                modifier = Modifier.standardKeyNavigation(focusManager, up = false),
                isError = state.value.username.error != null,
                errorText = state.value.username.error,
                required = true,
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
                onValidate = { forceValid ->
                    component.validatePassword(forceValid)
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
                    onValidate = { forceValid ->
                        component.validateVerificationCode(forceValid)
                    }
                )

                Spacer(modifier = Modifier.padding(10.dp))
                Text("Need a new verification code?")
                Button(
                    onClick = {
                        component.getNewCode { exception ->
                            if (exception != null) {
                                val snackbarMessage = when (exception) {
                                    is NetworkRequestError -> {
                                        "${exception.failureReason!!}${if (exception.recoverySuggestion != null) " - ${exception.recoverySuggestion!!}" else ""}"
                                    }
                                    else -> exception.message
                                }
                                showSnackbar(snackbarMessage ?: "")
                            }
                            else {
                                showSnackbar("Check your email for a new verification code - don't forget the Junk folder")
                            }
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