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

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Shapes
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.example.baselineprofiles_codelab.ui.theme.JetsnackColors
import com.example.baselineprofiles_codelab.ui.theme.JetsnackTheme

/**
 * A wrapper around [Scaffold] that provides defaults for Jetsnack.
 *
 * This composable provides a basic structure for screens in the Jetsnack app. It leverages the
 * Material Design [Scaffold] and sets default values and theming to match the Jetsnack style,
 * calling [Scaffold] with the parameters passed [JetsnackScaffold] as its arguments.
 *
 * @param modifier [Modifier] to be applied to the layout, defaults to the empty, default, or
 * starter [Modifier] that contains no elements. .
 * @param scaffoldState The state object to be used to control the scaffold's state, defaults to
 * an empty [rememberScaffoldState] instance.
 * @param topBar The top app bar to be displayed, defaults to an empty composable.
 * @param bottomBar The bottom bar to be displayed, defaults to an empty composable.
 * @param snackbarHost The composable to display snackbars, defaults to a lambda that composes
 * a [SnackbarHost] whose `hostState` is the [SnackbarHostState] passed the lambda.
 * @param floatingActionButton The floating action button to be displayed, defaults to an empty
 * composable.
 * @param floatingActionButtonPosition The position of the floating action button, defaults to
 * [FabPosition.End].
 * @param isFloatingActionButtonDocked [Boolean] Whether the floating action button should be docked,
 * defaults to `false`.
 * @param drawerContent The content of the drawer. If `null`, no drawer will be shown, defaults to
 * `null`.
 * @param drawerShape The shape of the drawer, defaults to the [Shapes.large] of our custom
 * [MaterialTheme.shapes].
 * @param drawerElevation The elevation of the drawer, defaults to [DrawerDefaults.Elevation].
 * @param drawerBackgroundColor The background color of the drawer, defaults to the
 * [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors].
 * @param drawerContentColor The content color of the drawer, defaults to the
 * [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors].
 * @param drawerScrimColor The scrim color of the drawer, defaults to the [JetsnackColors.uiBorder]
 * of our custom [JetsnackTheme.colors].
 * @param backgroundColor The background color of the content, defaults to the
 * [JetsnackColors.uiBackground] of our custom [JetsnackTheme.colors].
 * @param contentColor The preferred content color provided by this layout, defaults to the
 * [JetsnackColors.textSecondary] of our custom [JetsnackTheme.colors].
 * @param content The main content of the screen should be a lambda that takes a [PaddingValues]
 * parameter that can be used to compute the inner padding applied to the content.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun JetsnackScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    topBar: @Composable (() -> Unit) = {},
    bottomBar: @Composable (() -> Unit) = {},
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(hostState = it) },
    floatingActionButton: @Composable (() -> Unit) = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    isFloatingActionButtonDocked: Boolean = false,
    drawerContent: @Composable (ColumnScope.() -> Unit)? = null,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = JetsnackTheme.colors.uiBackground,
    drawerContentColor: Color = JetsnackTheme.colors.textSecondary,
    drawerScrimColor: Color = JetsnackTheme.colors.uiBorder,
    backgroundColor: Color = JetsnackTheme.colors.uiBackground,
    contentColor: Color = JetsnackTheme.colors.textSecondary,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        isFloatingActionButtonDocked = isFloatingActionButtonDocked,
        drawerContent = drawerContent,
        drawerShape = drawerShape,
        drawerElevation = drawerElevation,
        drawerBackgroundColor = drawerBackgroundColor,
        drawerContentColor = drawerContentColor,
        drawerScrimColor = drawerScrimColor,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        content = content
    )
}
