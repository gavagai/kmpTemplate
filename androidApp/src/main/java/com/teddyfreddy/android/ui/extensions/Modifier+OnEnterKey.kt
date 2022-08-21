package com.teddyfreddy.android.ui.extensions

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.NativeKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onEnterKey(
    onEnter: () -> Unit
) : Modifier {
    return this.then(
        Modifier.onPreviewKeyEvent {
            if (it.key == Key.Enter && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                onEnter()
                false
            }
            else {
                false
            }
        }
    )
}