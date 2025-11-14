package com.example.examplecomposegrid.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * A set of Material Design typography styles to be used by the app.
 *
 * This [Typography] object defines the text styles for different elements
 * in the application, such as body text, buttons, and captions. These styles
 * can be customized to match the app's branding and design language.
 *
 * The default `body1` style is configured here, and other styles like
 * `button` and `caption` are commented out as examples of further customization.
 *
 * @see androidx.compose.material.Typography
 */
val Typography: Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)