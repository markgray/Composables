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

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.codelab.basiclayouts.R

/**
 * Font family that contains a [Font] created from the kulim_park_light.ttf font file contained in
 * our resources (resource ID [R.font.kulim_park_regular]), and a [Font] created from the
 * kulim_park_light.ttf font file contained in our resources (resource ID [R.font.kulim_park_light])
 * whose `weight` is [FontWeight.Light] (The system uses this to match a font to a font request).
 * The [Font] built from [R.font.kulim_park_regular] is used for the `h2`, and `caption` [TextStyle]
 * of our [typography] custom [Typography], the [Font] built from [R.font.kulim_park_light] is used
 * for the `h1` [TextStyle] (because its `fontWeight` is specified to be [FontWeight.Light]).
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
 * It is used as the `defaultFontFamily` of our [typography] custom [Typography], and because of
 * this the [Font] built from [R.font.lato_regular] is used for the `body1` [TextStyle]
 * of our [typography] custom [Typography], and the [Font] built from [R.font.lato_bold] is used
 * for the `h3` and `button` [TextStyle] (because their `fontWeight` is specified to be
 * [FontWeight.Bold]).
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
 *  - `defaultFontFamily` - the default FontFamily to be used for TextStyles provided in the
 *  constructor is [fontFamilyLato]. This default will be used if the FontFamily of the [TextStyle]
 *  is `null`.
 *  - `h1` - `h1` is the largest headline, reserved for short, important text or numerals. We use
 *  a [TextStyle] whose `fontFamily` is [fontFamilyKulim], whose `fontWeight` is [FontWeight.Light],
 *  whose `fontSize` is 28.sp, and whose `letterSpacing` is (1.15).sp
 *  - `h2` - `h2` is the second largest headline, reserved for short, important text or numerals. We
 *  use a [TextStyle] whose `fontFamily` is [fontFamilyKulim], whose `fontSize` is 28.sp, and whose
 *  `letterSpacing` is (1.15).sp
 *  - `h3` - `h3` is the third largest headline, reserved for short, important text or numerals. We
 *  use a [TextStyle] whose `fontFamily` is the default font family [fontFamilyLato], whose `fontWeight`
 *  is [FontWeight.Bold], whose `fontSize` is 14.sp, and whose `letterSpacing` is 0.sp
 *  - `body1` - `body1` is the largest body, and is typically used for long-form writing as it works
 *  well for small text sizes. We use a [TextStyle] whose `fontFamily` is the default font family
 *  [fontFamilyLato], whose `fontSize` is 14.sp, and whose `letterSpacing` is 0.sp
 *  - `button` - `button` text is a call to action used in different types of buttons (such as text,
 *  outlined and contained buttons) and in tabs, dialogs, and cards. We use a [TextStyle] whose
 *  `fontFamily` is the default font family [fontFamilyLato], whose `fontSize` is 14.sp, and whose
 *  `letterSpacing` is (1.15).sp
 *  - caption - caption is one of the smallest font sizes. It is used sparingly to annotate imagery
 *  or to introduce a headline. We use a [TextStyle] whose `fontFamily` is [fontFamilyKulim], whose
 *  `fontSize` is 12.sp, and whose `letterSpacing` is (1.15).sp
 */
val typography: Typography = Typography(
    defaultFontFamily = fontFamilyLato,
    h1 = TextStyle(
        fontFamily = fontFamilyKulim,
        fontWeight = FontWeight.Light,
        fontSize = 28.sp,
        letterSpacing = (1.15).sp
    ),
    h2 = TextStyle(
        fontFamily = fontFamilyKulim,
        fontSize = 15.sp,
        letterSpacing = (1.15).sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    body1 = TextStyle(
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    button = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = (1.15).sp
    ),
    caption = TextStyle(
        fontFamily = fontFamilyKulim,
        fontSize = 12.sp,
        letterSpacing = (1.15).sp
    ),
)
