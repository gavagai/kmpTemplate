package com.teddyfreddy.android.ui.extensions

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.*

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.standardKeyNavigation(
    focusManager: FocusManager,
    up: Boolean = true,
    down: Boolean = true,
    enterMeansDown: Boolean = true
) : Modifier {
    return this.then(
        Modifier.onPreviewKeyEvent {
            if (it.key == Key.Tab && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                if (up && it.isShiftPressed) {
                    focusManager.moveFocus(FocusDirection.Up)
                    true
                }
                else if (down && !it.isShiftPressed) {
                    focusManager.moveFocus(FocusDirection.Down)
                    true
                }
                else {
                    false
                }
            }
            else if (it.key == Key.Enter && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                if (down && enterMeansDown) {
                    focusManager.moveFocus(FocusDirection.Down)
                    true
                }
                else {
                    false
                }
            }
            else if (it.key == Key.DirectionDown && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                if (down) {
                    focusManager.moveFocus(FocusDirection.Down)
                    true
                }
                else {
                    false
                }
            }
            else if (it.key == Key.DirectionUp && it.nativeKeyEvent.action == NativeKeyEvent.ACTION_DOWN) {
                if (up) {
                    focusManager.moveFocus(FocusDirection.Up)
                    true
                }
                else {
                    false
                }
            }
            else {
                false
            }
        }
    )
}