package com.example.examplecomposegrid.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

/**
 * Defines the shapes used throughout the application, overriding the default Material Design shapes.
 * This includes shapes for small, medium, and large components.
 */
val Shapes: Shapes = Shapes(
    small = RoundedCornerShape(size = 4.dp),
    medium = RoundedCornerShape(size = 4.dp),
    large = RoundedCornerShape(size = 0.dp)
)