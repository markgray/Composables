/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetnews.ui.modifiers

import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType.Companion.KeyUp
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

/**
 * Intercepts a [KeyEvent] when the [KeyEvent.key] is our [Key] parameter [key] rather than passing
 * it on to children and calls our [onKeyEvent] lambda parameter instead. We only pass the key event
 * to children if it's not the chosen key. To do this we chain a [Modifier.onPreviewKeyEvent] to our
 * [Modifier] receiver and in its `onPreviewKeyEvent` lambda argument we check the [KeyEvent] passed
 * the lambda to see if the [KeyEvent.key] is equal to our [Key] parameter [key], and the [KeyEvent.type]
 * is [KeyUp] and if so we call our lambda parameter [onKeyEvent] and return `true` to consume the
 * event. Otherwise we return the value of comparing the [KeyEvent.key] to our [Key] parameter [key]
 * (so we only return `false` and pass the key event to children if it's not the chosen key no matter
 * what the [KeyEvent.type] is).
 *
 * @param key the [Key] we are to intercept. Our user `HomeSearch` calls us with [Key.Enter].
 * @param onKeyEvent the lambda we should call when the [KeyEvent.key] of the [KeyEvent] passed us
 * is equal to our [Key] parameter [key] (and the [KeyEvent.type] is [KeyUp]). Our user `HomeSearch`
 * passes us a lambda which calls the `submitSearch` function to simulate a search, hides the keyboard
 * and clears the focus of its [OutlinedTextField].
 */
fun Modifier.interceptKey(key: Key, onKeyEvent: () -> Unit): Modifier {
    return this.onPreviewKeyEvent { keyEvent: KeyEvent ->
        if (keyEvent.key == key && keyEvent.type == KeyUp) { // fire onKeyEvent on KeyUp to prevent duplicates
            onKeyEvent()
            true
        } else keyEvent.key == key // only pass the key event to children if it's not the chosen key
    }
}
