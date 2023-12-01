package com.codelab.theming.ui.start.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

/**
 * The [Shapes] used by the [JetnewsTheme] custom [MaterialTheme]. The `small` [RoundedCornerShape]
 * of 8.dp is used by Small components, the `medium` [RoundedCornerShape] of 24.dp is used by medium
 * components, and the `large` [RoundedCornerShape] of 8.dp is used by large components
 */
val JetnewsShapes: Shapes = Shapes(
    small = CutCornerShape(topStart = 8.dp),
    medium = CutCornerShape(topStart = 24.dp),
    large = RoundedCornerShape(8.dp)
)
