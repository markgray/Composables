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

package com.example.baselineprofiles_codelab.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * A customized Snackbar composable based on Material Design's Snackbar. This function provides a
 * themed Snackbar with pre-defined colors and shapes specific to the Jetsnack application.
 *
 * @param snackbarData The data that defines the content of this Snackbar. Typically created and
 * managed by a [SnackbarHost].
 * @param modifier Optional [Modifier] for this Snackbar.
 * @param actionOnNewLine Whether or not the action should be put on a new line.
 * @param shape The shape of the snackbar. Defaults to [Shapes.small] of our custom
 * [MaterialTheme.shapes].
 * @param backgroundColor The background color of this Snackbar. Defaults to the
 * [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors].
 * @param contentColor The color of the text in the Snackbar. Defaults to the
 * [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors].
 * @param actionColor The color of the action text in the Snackbar. Defaults to the
 * [JetsnackColors.brand] of our custom [JetsnackTheme.colors].
 * @param elevation The elevation of this Snackbar. Defaults to 6.dp.
 */
@Composable
fun JetsnackSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textSecondary,
    actionColor: Color = JetsnackTheme.colors.brand,
    elevation: Dp = 6.dp
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        actionColor = actionColor,
        elevation = elevation
    )
}
