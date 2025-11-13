package com.example.android.colorinm3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun colorInfoList(): List<ColorInfo> {
    val infoList: List<ColorInfo> = listOf(
        ColorInfo(
            color = MaterialTheme.colorScheme.primary,
            colorName = "primary"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.primaryContainer,
            colorName = "primary"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.secondary,
            colorName = "secondary"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.secondaryContainer,
            colorName = "secondary"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.tertiary,
            colorName = "tertiary"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.tertiaryContainer,
            colorName = "tertiaryContainer"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.error,
            colorName = "error"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.errorContainer,
            colorName = "errorContainer"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.background,
            colorName = "background"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.surface,
            colorName = "surface"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.surfaceVariant,
            colorName = "surfaceVariant"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.inverseSurface,
            colorName = "inverseSurface"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.inversePrimary,
            colorName = "surface"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.surfaceTint,
            colorName = "surfaceTint"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.outlineVariant,
            colorName = "outlineVariant"
        ),
        ColorInfo(
            color = MaterialTheme.colorScheme.scrim,
            colorName = "scrim"
        )
    )

    return infoList
}

data class ColorInfo(
    val color: Color = Color.White,
    val colorName: String = "White"
)
