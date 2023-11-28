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

package com.codelab.basiclayouts.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.codelab.basiclayouts.R

/**
 * Font family that contains a [Font] created from the kulim_park_light.ttf font file contained in
 * our resources (resource ID [R.font.kulim_park_regular]), and a [Font] created from the
 * kulim_park_light.ttf font file contained in our resources (resource ID [R.font.kulim_park_light])
 * whose `weight` is [FontWeight.Light] (The system uses this to match a font to a font request).
 * The [Font] built from [R.font.kulim_park_regular] is used for the `displayMedium`, and
 * `displaySmall` [TextStyle] of our [typography] custom [Typography], the [Font] built from
 * [R.font.kulim_park_light] is used for the `displayLarge` [TextStyle] (because its `fontWeight`
 * is specified to be [FontWeight.Light]).
 */
private val fontFamilyKulim = FontFamily(
    listOf(
        Font(
            resId = R.font.kulim_park_regular
        ),
        Font(
            resId = R.font.kulim_park_light,
            weight = FontWeight.Light
        )
    )
)

/**
 * Font family that contains a [Font] created from the lato_regular.ttf font file contained in
 * our resources (resource ID [R.font.lato_regular]), and a [Font] created from the
 * lato_bold.ttf font file contained in our resources (resource ID [R.font.lato_bold])
 * whose `weight` is [FontWeight.Bold] (The system uses this to match a font to a font request)
 * It is used for the `titleMedium` (Bold), `bodySmall` (Regular), `bodyMedium` (Regular),
 * `bodyLarge` (Regular), `labelMedium` (Bold), and `labelLarge` (Regular) [TextStyle]s of our
 * [typography] custom [Typography].
 */
private val fontFamilyLato = FontFamily(
    listOf(
        Font(
            resId = R.font.lato_regular
        ),
        Font(
            resId = R.font.lato_bold,
            weight = FontWeight.Bold
        )
    )
)

/**
 * Set of Material typography styles to use. The parameters to the [Typography] constructor are:
 *  - `displayLarge` is the largest display text.
 *  - `displayMedium` is the second largest display text.
 *  - `displaySmall` is the smallest display text.
 *  - `headlineLarge` is the largest headline, reserved for short, important text or numerals. For
 *  headlines, you can choose an expressive font, such as a display, handwritten, or script style.
 *  These unconventional font designs have details and intricacy that help attract the eye.
 *  - `headlineMedium` is the second largest headline, reserved for short, important text or
 *  numerals. For headlines, you can choose an expressive font, such as a display, handwritten,
 *  or script style. These unconventional font designs have details and intricacy that help attract
 *  the eye.
 *  - `headlineSmall` is the smallest headline, reserved for short, important text or numerals.
 *  For headlines, you can choose an expressive font, such as a display, handwritten, or script
 *  style. These unconventional font designs have details and intricacy that help attract the eye.
 *  - `titleLarge` is the largest title, and is typically reserved for medium-emphasis text that is
 *  shorter in length. Serif or sans serif typefaces work well for subtitles.
 *  - `titleMedium` is the second largest title, and is typically reserved for medium-emphasis text
 *  that is shorter in length. Serif or sans serif typefaces work well for subtitles.
 *  - `titleSmall` is the smallest title, and is typically reserved for medium-emphasis text that
 *  is shorter in length. Serif or sans serif typefaces work well for subtitles.
 *  - `bodyLarge` is the largest body, and is typically used for long-form writing as it works well
 *  for small text sizes. For longer sections of text, a serif or sans serif typeface is recommended.
 *  - `bodyMedium` is the second largest body, and is typically used for long-form writing as it
 *  works well for small text sizes. For longer sections of text, a serif or sans serif typeface
 *  is recommended.
 *  - `bodySmall` is the smallest body, and is typically used for long-form writing as it works well
 *  for small text sizes. For longer sections of text, a serif or sans serif typeface is recommended.
 *  - `labelLarge` text is a call to action used in different types of buttons (such as text,
 *  outlined and contained buttons) and in tabs, dialogs, and cards. Button text is typically sans
 *  serif, using all caps text.
 *  - `labelMedium` is one of the smallest font sizes. It is used sparingly to annotate imagery or
 *  to introduce a headline.
 *  `labelSmall` is one of the smallest font sizes. It is used sparingly to annotate imagery or to
 *  introduce a headline.
 */
val typography: Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = fontFamilyKulim,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = fontFamilyKulim,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = fontFamilyKulim,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    titleMedium = TextStyle(
        fontFamily = fontFamilyLato,
        fontWeight = FontWeight(500),
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (0.15).sp
    ),
    bodySmall = TextStyle(
        fontFamily = fontFamilyLato,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = (0.4).sp
    ),
    bodyMedium = TextStyle(
        fontFamily = fontFamilyLato,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (0.25).sp
    ),
    bodyLarge = TextStyle(
        fontFamily = fontFamilyLato,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (0.5).sp
    ),
    labelMedium = TextStyle(
        fontFamily = fontFamilyLato,
        fontWeight = FontWeight(500),
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = (0.5).sp,
        textAlign = TextAlign.Center
    ),
    labelLarge = TextStyle(
        fontFamily = fontFamilyLato,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (0.1).sp
    )
)
