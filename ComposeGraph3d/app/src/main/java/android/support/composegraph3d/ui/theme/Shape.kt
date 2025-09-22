package android.support.composegraph3d.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

/**
 * The [MaterialTheme.shapes] of our custom [MaterialTheme].
 */
val Shapes: Shapes = Shapes(
    small = RoundedCornerShape(size = 4.dp),
    medium = RoundedCornerShape(size = 4.dp),
    large = RoundedCornerShape(size = 0.dp)
)