package com.example.android.colorinm3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * A composable function that returns a list of [ColorInfo] objects,
 * each representing a key color from the current [MaterialTheme.colorScheme].
 *
 * This function is useful for displaying a palette of the theme's colors,
 * for example, in a design system viewer or a theme customization screen.
 * Each [ColorInfo] contains the [Color] value and its corresponding name (e.g., "primary").
 *
 * @return A `List<ColorInfo>` containing various colors from the Material 3 theme.
 */
@Composable
fun colorInfoList(): List<ColorInfo> {
    val infoList: List<ColorInfo> = listOf(
        ColorInfo(
            color = MaterialTheme.colorScheme.primary,
            name = "primary"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.primaryContainer,
            name = "primaryContainer"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.secondary,
            name = "secondary"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.secondaryContainer,
            name = "secondaryContainer"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.tertiary,
            name = "tertiary"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            name = "tertiaryContainer"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.error,
            name = "error"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.errorContainer,
            name = "errorContainer"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.background,
            name = "background"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.surface,
            name = "surface"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.surfaceVariant,
            name = "surfaceVariant"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.inverseSurface,
            name = "inverseSurface"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.inversePrimary,
            name = "inversePrimary"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.surfaceTint,
            name = "surfaceTint"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.outlineVariant,
            name = "outlineVariant"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.scrim,
            name = "scrim"
        )
    )
    return infoList
}

/**
 * A data class that holds information about a color, including its [Color] value
 * and its descriptive [name]. This is used to display color swatches with their
 * corresponding names in the UI.
 *
 * @property color The [Color] value. Defaults to [Color.White].
 * @property name The name of the color, typically its role in the theme (e.g., "primary").
 * Defaults to "White".
 */
data class ColorInfo(
    val color: Color = Color.White,
    val name: String = "White"
)
