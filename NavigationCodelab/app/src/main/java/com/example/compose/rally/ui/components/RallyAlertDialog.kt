/*
 * Copyright 2022 The Android Open Source Project
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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.compose.rally.ui.overview.AlertCard
import com.example.compose.rally.ui.overview.AlertHeader
import com.example.compose.rally.ui.theme.RallyDialogThemeOverlay

/**
 * This custom [AlertDialog] is popped up when the clicks on the "SEE ALL" [TextButton] in the
 * [AlertHeader] Composable of the [AlertCard] within the Rally Overview screen. Our `content` is
 * just an [AlertDialog] wrapped in our [RallyDialogThemeOverlay] custom [MaterialTheme]. The
 * `onDismissRequest` argument of the [AlertDialog] is our [onDismiss] parameter (it will be called
 * if the user tries to dismiss the Dialog by clicking outside or pressing the back button), the
 * `text` argument of the [AlertDialog] is a [Text] displaying our [bodyText] parameter, and the
 * `buttons` argument is a Composable lambda consisting of a [Column] that holds a [Divider] with
 * default values for its `thickness` (1.dp) and `startIndent` (0.dp), a [Modifier.padding] that
 * adds 12.dp to each end of the [Divider], and whose `color` is a copy of the [Colors.onSurface]
 * [Color] with an alpha of 0.2f, and below it in the [Column] is a [TextButton] whose `onClick`
 * argument is our [onDismiss] parameter, whose `shape` argument is [RectangleShape], whose
 * `contentPadding` is a [PaddingValues] that adds 16.dp to all sides and whose `modifier` argument
 * is a [Modifier.fillMaxWidth] to have it fill its entire incoming width constraint. The `content`
 * label of the [TextButton] is a [Text] displaying our [buttonText] parameter ("DISMISS").
 *
 * @param onDismiss the lambda to be called when the user decides to dismiss the [AlertDialog], it
 * is used for both the `onDismissRequest` argument of the [AlertDialog] where it will be called if
 * the user tries to dismiss the Dialog by clicking outside or pressing the back button and as the
 * `onClick` argument of the "Dismiss" [TextButton] in the [AlertDialog].
 * @param bodyText the `text` argument of the [AlertDialog], in our case this is "Heads up, you've
 * used up 90% of your Shopping budget for this month."
 * @param buttonText the `text` argument of the [Text] that is used as the label of the [TextButton]
 * in the [AlertDialog], in our case the "Dismiss" string converted to upper case using the rules of
 * the current value of the default locale.
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
            text = { Text(bodyText) },
            buttons = {
                Column {
                    Divider(
                        Modifier.padding(horizontal = 12.dp),
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
                    )
                    TextButton(
                        onClick = onDismiss,
                        shape = RectangleShape,
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(buttonText)
                    }
                }
            }
        )
    }
}
