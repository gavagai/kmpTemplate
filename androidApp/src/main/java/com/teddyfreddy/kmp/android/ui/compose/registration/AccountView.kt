package com.teddyfreddy.kmp.android.ui.compose.registration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.teddyfreddy.android.ui.extensions.EmailTextField
import com.teddyfreddy.kmp.decompose.registration.Account
import com.teddyfreddy.android.ui.extensions.ValidatedTextField
import com.teddyfreddy.android.ui.extensions.standardKeyNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountView(
    component: Account,
    modifier: Modifier? = Modifier
) {
    val state = component.model.subscribeAsState()

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
        EmailTextField(
            value = state.value.email.data,
            onValueChange = {
                component.changeEmail(it)
            },
            modifier = Modifier.standardKeyNavigation(focusManager, up = false),
            errorText = state.value.email.error,
            isError = state.value.email.error != null,
            required = true,
            decorations = false,
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            },
            onFocusChange = { focused ->
                component.focusChangeEmail(focused)
            }
        )

        ValidatedTextField(
            value = state.value.password.data,
            onValueChange = {
                component.changePassword(it)
            },
            modifier = Modifier.standardKeyNavigation(focusManager),
            label = { Text("Password") },
            placeholder =  { Text("Password*") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            errorText = state.value.password.error,
            isError = state.value.password.error != null,
            required = true,
            onFocusChange = { focused ->
                component.focusChangePassword(focused)
            }
        )

        ValidatedTextField(
            value = state.value.passwordConfirmation.data,
            onValueChange = {
                component.changePasswordConfirmation(it)
            },
            modifier = Modifier.standardKeyNavigation(focusManager),
            label = { Text("Password confirmation") },
            placeholder =  { Text("Retype your password*") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            errorText = state.value.passwordConfirmation.error,
            isError = state.value.passwordConfirmation.error != null,
            required = true,
            onFocusChange = { focused ->
                component.focusChangePasswordConfirmation(focused)
            }
        )

        ValidatedTextField(
            value = state.value.givenName.data,
            onValueChange = {
                component.changeFirstName(it)
            },
            modifier = Modifier.standardKeyNavigation(focusManager),
            label = { Text("First name") },
            placeholder =  { Text("First name*") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            errorText = state.value.givenName.error,
            isError = state.value.givenName.error != null,
            required = true,
            onFocusChange = { focused ->
                component.focusChangeFirstName(focused)
            }
        )

        ValidatedTextField(
            value = state.value.familyName.data,
            onValueChange = {
                component.changeLastName(it)
            },
            modifier = Modifier.standardKeyNavigation(focusManager),
            label = { Text("Last name") },
            placeholder =  { Text("Last name*") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Words,
                autoCorrect = false,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            errorText = state.value.familyName.error,
            isError = state.value.familyName.error != null,
            required = true,
            onFocusChange = { focused ->
                component.focusChangeLastName(focused)
            }
        )

        TextField(
            value = state.value.phone,
            onValueChange = {
                component.changePhoneNumber(it)
            },
            modifier = Modifier.standardKeyNavigation(focusManager, down = false),
            label = { Text("Phone number") }
        )

        Spacer(modifier = Modifier.padding(20.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = { component.cancelPressed() }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = { component.continuePressed() }) {
                Text("Continue")
            }
        }
    }
}