package com.teddyfreddy.kmp.android.ui.registration

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
import com.teddyfreddy.kmp.android.ui.extensions.ValidatedTextField
import com.teddyfreddy.kmp.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountView(
    vm: AccountComposeViewModel,
    modifier: Modifier? = Modifier
) {
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
        ValidatedTextField(
            value = vm.model.value.email.value as? String ?: "",
            onValueChange = {
                vm.changeField(AccountViewField.UsernameField, it)
            },
            label = { Text("Email") },
            placeholder =  { Text("Email*") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    vm.validateField(AccountViewField.UsernameField)
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            errorText = vm.model.value.email.error,
            isError = vm.model.value.email.error != null,
            required = true
        )

        ValidatedTextField(
            value = vm.model.value.password.value as? String ?: "",
            onValueChange = {
                vm.changeField(AccountViewField.PasswordField, it)
            },
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
                    vm.validateField(AccountViewField.PasswordField)
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            errorText = vm.model.value.password.error,
            isError = vm.model.value.password.error != null,
            required = true
        )

        ValidatedTextField(
            value = vm.model.value.passwordConfirmation.value as? String ?: "",
            onValueChange = {
                vm.changeField(AccountViewField.PasswordConfirmationField, it)
            },
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
                    vm.validateField(AccountViewField.PasswordConfirmationField)
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            errorText = vm.model.value.passwordConfirmation.error,
            isError = vm.model.value.passwordConfirmation.error != null,
            required = true
        )

        ValidatedTextField(
            value = vm.model.value.givenName.value as? String ?: "",
            onValueChange = {
                vm.changeField(AccountViewField.FirstNameField, it)
            },
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
                    vm.validateField(AccountViewField.FirstNameField)
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            errorText = vm.model.value.givenName.error,
            isError = vm.model.value.givenName.error != null,
            required = true
        )

        ValidatedTextField(
            value = vm.model.value.familyName.value as? String ?: "",
            onValueChange = {
                vm.changeField(AccountViewField.LastNameField, it)
            },
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
                    vm.validateField(AccountViewField.LastNameField)
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            errorText = vm.model.value.familyName.error,
            isError = vm.model.value.familyName.error != null,
            required = true
        )

        TextField(
            value = vm.model.value.phone ?: "",
            onValueChange = {
                vm.changeField(AccountViewField.PhoneNumberField, it)
            },
            label = { Text("Phone number") }
        )

        Spacer(modifier = Modifier.padding(20.dp))
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            Button(onClick = { vm.cancelPressed() }) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.padding(10.dp))
            Button(onClick = { vm.continuePressed() }) {
                Text("Continue")
            }
        }
    }
}