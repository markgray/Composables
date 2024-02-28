/*
 * Copyright 2020 The Android Open Source Project
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

package com.example.compose.jetchat

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

/**
 * This custom [AlertDialog] is "popped up" in several places when the user tries to use a feature
 * for which there is no implementation written yet. It consists of an [AlertDialog] whose
 * `onDismissRequest` argument is our lambda parameter [onDismiss], whose `text` argument is a
 * lambda that composes a [Text] whose `text` argument is "Functionality not available \uD83D\uDE48",
 * using as its `style` the [TextStyle] of the [Typography.bodyMedium] of our custom
 * [MaterialTheme.typography]. The `confirmButton` argument of the [AlertDialog] is a lambda that
 * composes a [TextButton] whose `onClick` argument is our [onDismiss] lambda parameter, and with
 * its `content` lambda composing a [Text] whose `text` argument is the [String] "CLOSE".
 *
 * @param onDismiss a lambda that our [AlertDialog] can call to dismiss this
 * [FunctionalityNotAvailablePopup] either because the user tries to dismiss the Dialog by clicking
 * outside or pressing the back button (the `onDismissRequest` argument of the [AlertDialog]) or
 * by clicking the `confirmButton` argument of the [AlertDialog].
 */
@Composable
fun FunctionalityNotAvailablePopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = "Functionality not available \uD83D\uDE48",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "CLOSE")
            }
        }
    )
}
