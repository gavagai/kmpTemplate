package com.teddyfreddy.android.ui.extensions

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldWithSupportingText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.filledShape,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    supportingText: String? = null
) {
    Column(horizontalAlignment = Alignment.Start) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors
        )
        if (supportingText != null) {
            Text(
                text = supportingText,
                color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified,
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.filledShape,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    supportingText: String? = null,
    errorText: String? = null,
    required: Boolean? = false,
    onValidate: ((Boolean) -> Unit)? = null
) {
    var focused by remember { mutableStateOf(false) }

    fun isRequired(): Boolean {
        return required != null && required
    }
    fun computeSupportingText(): String {
        return if (isError && errorText != null) {
            errorText
        } else if (focused && isRequired() && value.isEmpty()) {
            supportingText ?: "*required"
        } else {
            supportingText ?: ""
        }
    }

    val computedTrailingIcon: @Composable (() -> Unit)? =
        if (trailingIcon != null || !isError) trailingIcon else { { Icon(Icons.Filled.Error, "error") } }

    TextFieldWithSupportingText(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = modifier.onFocusChanged {
            if (it.isFocused) {
                if (value.isEmpty()) {
                    if (onValidate != null) onValidate(true)
                }
            }
            else {
                if (focused) { // Skip initial redundant "unfocus"
                    if (onValidate != null) onValidate(false)
                }
            }
            focused = it.isFocused
        },
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = computedTrailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        supportingText = computeSupportingText()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsernameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    errorText: String? = null,
    required: Boolean = false,
    decorations: Boolean = true,
    onNext: (() -> Unit)?,
    onValidate: ((Boolean) -> Unit)? = null
) {
    ValidatedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label ?: { Text("Username") },
        placeholder = placeholder ?: { if (required) { Text("Username*") } else { Text("Username") } },
        leadingIcon = if (decorations) { leadingIcon ?: { Icon(Icons.Default.Person, "Username") } } else null,
        trailingIcon = if (decorations) trailingIcon else null,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            capitalization = KeyboardCapitalization.None,
            autoCorrect = false,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { if (onNext != null) onNext() }
        ),
        supportingText = supportingText,
        errorText = errorText ?: "Please enter your username",
        required = required,
        onValidate = onValidate
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    errorText: String? = null,
    required: Boolean = false,
    decorations: Boolean = true,
    showPassword: Boolean = false,
    onNext: (() -> Unit)? = null,
    onGo: (() -> Unit)? = null,
    onValidate: ((Boolean) -> Unit)? = null
) {
    ValidatedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onPreviewKeyEvent {
                if (it.key == Key.Enter && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                    if (onGo != null) onGo()
                    true
                } else {
                    false
                }
            },
        label = label ?: { Text("Password") },
        placeholder = placeholder ?: { if (required) { Text("Password*") } else { Text("Password") } },
        leadingIcon = if (decorations) { leadingIcon ?: { Icon(Icons.Default.Lock, "Password") } } else null,
        trailingIcon = if (decorations) { trailingIcon ?: {
            IconButton(
                onClick = { if (onGo != null) onGo() }
            ) {
                Icon(Icons.Outlined.ArrowCircleRight, "login")
            }
        } } else null,
        isError = isError,
        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            capitalization = KeyboardCapitalization.None,
            autoCorrect = false,
            imeAction = if (onNext != null) ImeAction.Next else ImeAction.Go
        ),
        keyboardActions = KeyboardActions(
            onNext = { if (onNext != null) onNext() },
            onGo = { if (onGo != null) onGo() }
        ),
        supportingText = supportingText,
        errorText = errorText ?: "Please enter your password",
        required = required,
        onValidate = onValidate
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OneTimeCodeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    errorText: String? = null,
    required: Boolean = false,
    decorations: Boolean = true,
    onNext: (() -> Unit)? = null,
    onGo: (() -> Unit)? = null,
    onValidate: ((Boolean) -> Unit)? = null
) {
    ValidatedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label ?: { Text("Verification code") },
        placeholder = placeholder ?: { if (required) { Text("Verification code*") } else { Text("Verification code") } },
        leadingIcon = if (decorations) { leadingIcon ?: { Icon(Icons.Default.Key, "verification code") } } else null,
        trailingIcon = if (decorations) { trailingIcon ?: {
            IconButton(
                onClick = { if (onGo != null) onGo() }
            ) {
                Icon(Icons.Outlined.ArrowCircleRight, "login")
            }
        } } else null,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            capitalization = KeyboardCapitalization.None,
            autoCorrect = false,
            imeAction = if (onNext != null) ImeAction.Next else ImeAction.Go
        ),
        keyboardActions = KeyboardActions(
            onNext = { if (onNext != null) onNext() },
            onGo = { if (onGo != null) onGo() }
        ),
        supportingText = supportingText,
        errorText = errorText ?: "Please enter the one time verification code that you were sent",
        required = required,
        onValidate = onValidate
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: String? = null,
    errorText: String? = null,
    required: Boolean = false,
    decorations: Boolean = true,
    onNext: (() -> Unit)? = null,
    onValidate: ((Boolean) -> Unit)? = null
) {
    ValidatedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label ?: { Text("Email") },
        placeholder = placeholder ?: { if (required) { Text("Email*") } else { Text("Email") } },
        leadingIcon = if (decorations) { leadingIcon ?: { Icon(Icons.Default.Mail, "Email") } } else null,
        trailingIcon = if (decorations) trailingIcon else null,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            capitalization = KeyboardCapitalization.None,
            autoCorrect = false,
            imeAction = if (onNext != null) ImeAction.Next else ImeAction.None
        ),
        keyboardActions = KeyboardActions(
            onNext = { if (onNext != null) onNext() }
        ),
        supportingText = supportingText,
        errorText = errorText ?: "Please enter your email address",
        required = required,
        onValidate = onValidate
    )
}



