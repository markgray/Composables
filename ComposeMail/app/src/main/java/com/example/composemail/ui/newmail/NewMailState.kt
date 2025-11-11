/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composemail.ui.newmail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Creates and remembers a [NewMailState] instance. The state is remembered across
 * recompositions. If [key] changes, the state will be reset to a new instance with
 * the provided [initialLayoutState].
 *
 * @param key An optional key to reset the state. If the key changes, the existing
 * [NewMailState] will be discarded and a new one will be created with the
 * [initialLayoutState].
 * @param initialLayoutState The initial state of the new mail layout.
 * @return A remembered [NewMailState] instance.
 */
@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun rememberNewMailState(
    key: Any = Unit,
    initialLayoutState: NewMailLayoutState
): NewMailState = remember(key1 = key) { NewMailState(initialLayoutState = initialLayoutState) }

/**
 * A state holder for the "New Mail" screen.
 *
 * This class manages the current layout state of the new mail composer, which can be one of
 * [NewMailLayoutState.Full], [NewMailLayoutState.Mini], or [NewMailLayoutState.Fab]. It provides
 * methods to transition between these states.
 *
 * @param initialLayoutState The initial state of the new mail composer.
 */
class NewMailState(initialLayoutState: NewMailLayoutState) {
    /**
     * The current layout state of the new mail composer.
     */
    private var _currentState: MutableState<NewMailLayoutState> =
        mutableStateOf(value = initialLayoutState)

    /**
     * The public read-only property that exposes the current state of the new mail composer, which
     * determines its layout.
     *
     * This property returns the current [NewMailLayoutState], which can be [NewMailLayoutState.Full],
     * [NewMailLayoutState.Mini], or [NewMailLayoutState.Fab]. Observing this state allows the UI
     * to react and change its layout accordingly.
     */
    val currentState: NewMailLayoutState
        get() = _currentState.value

    /**
     * Sets the current layout state to [NewMailLayoutState.Full].
     *
     * This transition is typically used when the user wants to expand the composer to a full-screen
     * view for writing or editing the email.
     */
    fun setToFull() {
        _currentState.value = NewMailLayoutState.Full
    }

    /**
     * Sets the current layout state to [NewMailLayoutState.Mini].
     *
     * This transition is typically used when the user minimizes the full-screen composer,
     * showing a smaller, persistent view of the draft at the bottom of the screen.
     */
    fun setToMini() {
        _currentState.value = NewMailLayoutState.Mini
    }

    /**
     * Sets the current layout state to [NewMailLayoutState.Fab].
     *
     * This transition is used to minimize the composer into a Floating Action Button (FAB).
     * It is typically triggered when the user wants to close or dismiss the composer view
     * without discarding the draft.
     */
    fun setToFab() {
        _currentState.value = NewMailLayoutState.Fab
    }
}

/**
 * Represents the different layout states of the new mail composer UI.
 *
 * This enum defines the possible visual states for the composer, allowing the UI to transition
 * between a full-screen editor, a minimized bar, and a floating action button.
 *
 * @property Full The full-screen composing state, where the user can write the email content
 * and see all fields like "To", "Subject", etc.
 * @property Mini A minimized state where the composer is shown as a small bar at the bottom of the
 * screen, keeping the draft accessible while the user navigates other parts of the app.
 * @property Fab A state where the composer is collapsed into a Floating Action Button (FAB). This
 * is the most compact state, typically used when the composer is closed but the draft is saved.
 */
enum class NewMailLayoutState {
    Full,
    Mini,
    Fab
}

// Reply, Forward, Archive, Delete