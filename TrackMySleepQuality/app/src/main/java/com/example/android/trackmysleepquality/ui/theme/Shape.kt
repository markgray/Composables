package com.example.android.trackmysleepquality.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

/**
 * This is the Shape scheme that is used by our [TrackMySleepQualityTheme] custom [MaterialTheme].
 * Small components and Medium components will use a [RoundedCornerShape] with the corners rounded
 * by 4.dp, while Large components will use 0.dp rounded corners.
 */
val Shapes: Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)