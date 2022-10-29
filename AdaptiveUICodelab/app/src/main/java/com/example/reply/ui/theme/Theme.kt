/*
 * Copyright 2022 Google LLC
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

package com.example.reply.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Material 3 color schemes
private val replyDarkColorScheme: ColorScheme = darkColorScheme(
    primary = replyDarkPrimary,
    onPrimary = replyDarkOnPrimary,
    primaryContainer = replyDarkPrimaryContainer,
    onPrimaryContainer = replyDarkOnPrimaryContainer,
    inversePrimary = replyDarkPrimaryInverse,
    secondary = replyDarkSecondary,
    onSecondary = replyDarkOnSecondary,
    secondaryContainer = replyDarkSecondaryContainer,
    onSecondaryContainer = replyDarkOnSecondaryContainer,
    tertiary = replyDarkTertiary,
    onTertiary = replyDarkOnTertiary,
    tertiaryContainer = replyDarkTertiaryContainer,
    onTertiaryContainer = replyDarkOnTertiaryContainer,
    error = replyDarkError,
    onError = replyDarkOnError,
    errorContainer = replyDarkErrorContainer,
    onErrorContainer = replyDarkOnErrorContainer,
    background = replyDarkBackground,
    onBackground = replyDarkOnBackground,
    surface = replyDarkSurface,
    onSurface = replyDarkOnSurface,
    inverseSurface = replyDarkInverseSurface,
    inverseOnSurface = replyDarkInverseOnSurface,
    surfaceVariant = replyDarkSurfaceVariant,
    onSurfaceVariant = replyDarkOnSurfaceVariant,
    outline = replyDarkOutline
)

private val replyLightColorScheme: ColorScheme = lightColorScheme(
    primary = replyLightPrimary,
    onPrimary = replyLightOnPrimary,
    primaryContainer = replyLightPrimaryContainer,
    onPrimaryContainer = replyLightOnPrimaryContainer,
    inversePrimary = replyLightPrimaryInverse,
    secondary = replyLightSecondary,
    onSecondary = replyLightOnSecondary,
    secondaryContainer = replyLightSecondaryContainer,
    onSecondaryContainer = replyLightOnSecondaryContainer,
    tertiary = replyLightTertiary,
    onTertiary = replyLightOnTertiary,
    tertiaryContainer = replyLightTertiaryContainer,
    onTertiaryContainer = replyLightOnTertiaryContainer,
    error = replyLightError,
    onError = replyLightOnError,
    errorContainer = replyLightErrorContainer,
    onErrorContainer = replyLightOnErrorContainer,
    background = replyLightBackground,
    onBackground = replyLightOnBackground,
    surface = replyLightSurface,
    onSurface = replyLightOnSurface,
    inverseSurface = replyLightInverseSurface,
    inverseOnSurface = replyLightInverseOnSurface,
    surfaceVariant = replyLightSurfaceVariant,
    onSurfaceVariant = replyLightOnSurfaceVariant,
    outline = replyLightOutline
)

/**
 * This is our custom [MaterialTheme] which we use to provide values to Material components such as
 * `Button` and `Checkbox` when they retrieve default values for their [ColorScheme], [Typography]
 * and [Shapes]. We use a `when` expression to set our [ColorScheme] variable `val replyColorScheme`
 * different values depending on the value of our parameters:
 *  - Our [Boolean] parameter [dynamicColor] is `true` and [Build.VERSION.SDK_INT] is greater than
 *  or equal to [Build.VERSION_CODES.S] we set `replyColorScheme` to [dynamicDarkColorScheme] if the
 *  [darkTheme] is `true` (the system is considered to be in 'dark theme') or [dynamicLightColorScheme]
 *  if [darkTheme] is `false` (these are color schemes based on the colors of the system wallpaper).
 *  - If use of dynamic color shemes is precluded we set `replyColorScheme` to [replyDarkColorScheme]
 *  if [darkTheme] is `true` or to [replyLightColorScheme] if it is `false`.
 *
 * We initialize our [View] variable `val view` to the current Compose [View] that is returned by
 * `LocalView.current`. If the [View.isInEditMode] method returns `false` (we are not being rendered
 * for a design tool) we initialize our [Window] variable `val currentWindow` to the current [Window]
 * for the activity, then we call the [SideEffect] Composable to schedule its `effect` lambda argument
 * to run when the current composition completes successfully ([SideEffect] can be used to apply side
 * effects to objects managed by the composition that are not backed by snapshots so as not to leave
 * those objects in an inconsistent state if the current composition operation fails. `effect` will
 * always be run on the composition's apply dispatcher. A [SideEffect] runs after every recomposition).
 * The `effect` lambda of the [SideEffect] sets the the color of the status bar of `currentWindow` to
 * the ARGB color of the [ColorScheme.primary] of `replyColorScheme`, and then uses the method
 * [WindowCompat.getInsetsController] to retrieve the single `WindowInsetsControllerCompat` of the
 * window this view is attached to, and sets its `isAppearanceLightStatusBars` property to our
 * [Boolean] parameter [darkTheme] (If `true`, changes the foreground color of the status bars to
 * light so that the items on the bar can be read clearly. If `false`, reverts to the default
 * appearance).
 *
 * Finally we call the [MaterialTheme] Composable with its `colorScheme` argument our [ColorScheme]
 * variable `replyColorScheme`, its `typography` argument our [replyTypography] custom [Typography]
 * and as its `content` argument our [content] parameter (the Composable hierarchy that we are
 * wrapping).
 *
 * @param darkTheme `true` if the system is considered to be in 'dark theme'.
 * @param dynamicColor if `true` we try to use the dynamic color scheme api that was introduced in
 * [Build.VERSION_CODES.S] to provide the [ColorScheme].
 * @param content the Composable hierarchy that we are wrapping.
 */
@Composable
fun ReplyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val replyColorScheme: ColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context: Context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> replyDarkColorScheme
        else -> replyLightColorScheme
    }
    val view: View = LocalView.current
    if (!view.isInEditMode) {
        /* getting the current window by tapping into the Activity */
        val currentWindow: Window = (view.context as? Activity)?.window
            ?: throw Exception("Not in an activity - unable to get Window reference")
        SideEffect {
            currentWindow.statusBarColor = replyColorScheme.primary.toArgb()
            WindowCompat.getInsetsController(currentWindow, view)
                .isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = replyColorScheme,
        typography = replyTypography,
        content = content
    )
}