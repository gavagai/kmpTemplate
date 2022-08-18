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
import com.teddyfreddy.android.ui.extensions.OneTimeCodeTextField
import com.teddyfreddy.kmp.android.ui.decompose.Login
import com.teddyfreddy.android.ui.extensions.PasswordTextField
import com.teddyfreddy.android.ui.extensions.UsernameTextField
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

    var loginErrorMessage: MutableState<String?> = mutableStateOf(null)

    fun doLogin() {
        component.login { exception ->
            if (exception != null) {
                when (exception) {
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
                        loginErrorMessage.value = "${exception.failureReason!!}${if (exception.recoverySuggestion != null) " - ${exception.recoverySuggestion!!}" else ""}"
                    }
                    else -> loginErrorMessage.value = exception.message
                }
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

    }


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
                        if (it.key == Key.Tab && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                            focusManager.moveFocus(if (it.isShiftPressed) FocusDirection.Up else FocusDirection.Down)
                            true
                        }
                        else {
                            false
                        }
                    },
                trailingIcon = if (state.value.emailVerificationRequired) { {} } else null,
                isError = state.value.password.error != null,
                errorText = state.value.password.error,
                onGo = if (!state.value.emailVerificationRequired) {
                    {
                        doLogin()
                    }
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
                    isError = state.value.verificationCode.error != null,
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
                    onClick = { component.getNewCode() }
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