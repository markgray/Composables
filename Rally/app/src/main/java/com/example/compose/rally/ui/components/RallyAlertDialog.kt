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

package com.example.compose.rally.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Colors
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.compose.rally.ui.theme.RallyDialogThemeOverlay

/**
 * A Rally-themed [AlertDialog] for displaying a message to the user.
 *
 * Wrapped in our [RallyDialogThemeOverlay] custom [MaterialTheme] our root composable is an
 * [AlertDialog] whose arguments are:
 *  - `onDismissRequest`: is our [onDismiss] lambda parameter.
 *  - `text`: is a [Text] whose `text` argument is our [String] parameter [bodyText].
 *  - `buttons`: is a lambda that composes a [Column] whose [ColumnScope] `content` composable lambda
 *  argument composes a [Divider] whose `modifier` argument is a [Modifier.padding] that adds `12.dp`
 *  padding to the horizontal sides, and whose `color` argument is a copy of the [Colors.onSurface]
 *  of our custom [MaterialTheme.colors] with an alpha of `0.2f`. Below the [Divider] is a [TextButton]
 *  whose `onClick` argument is our [onDismiss] lambda parameter, whose `shape` argument is a
 *  [RectangleShape], whose `contentPadding` argument is a [PaddingValues] that adds `16.dp` padding
 *  to all sides, and whose `modifier` argument is a [Modifier.fillMaxWidth]. In the [RowScope]
 *  `content` composable lambda argument of the [TextButton] we compose a [Text] whose `text`
 *  argument is our [String] parameter [buttonText].
 *
 * @param onDismiss Will be called when the user clicks the button or dismisses the dialog.
 * @param bodyText The text to display in the dialog.
 * @param buttonText The text to display on the button.
 */
@Composable
fun RallyAlertDialog(
    onDismiss: () -> Unit,
    bodyText: String,
    buttonText: String
) {
    RallyDialogThemeOverlay {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = { Text(text = bodyText) },
            buttons = {
                Column {
                    Divider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                    )
                    TextButton(
                        onClick = onDismiss,
                        shape = RectangleShape,
                        contentPadding = PaddingValues(all = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = buttonText)
                    }
                }
            }
        )
    }
}
