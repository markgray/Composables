package com.example.android.colorinm3

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ColorInfoList(): ColorInfo {
    return ColorInfo(
        color = MaterialTheme.colorScheme.primary,
        colorName = "primary"
    )
}

data class ColorInfo(
    val color: Color = Color.White,
    val colorName: String = "White"
)
