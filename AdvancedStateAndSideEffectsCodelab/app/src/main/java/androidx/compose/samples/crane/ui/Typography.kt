/*
 * Copyright 2020 The Android Open Source Project
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

package androidx.compose.samples.crane.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.samples.crane.R
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.samples.crane.base.CraneEditableUserInput
import androidx.compose.samples.crane.base.CraneBaseUserInput

/**
 * This [Font] is used when [FontWeight.Light] `fontWeight` is specified. The `h1` [TextStyle] is the
 * only [TextStyle] in the [craneTypography] custom [Typography] that uses it.
 */
private val light = Font(R.font.raleway_light, FontWeight.W300)

/**
 * This [Font] is used when [FontWeight.Normal] `fontWeight` is specified. The [TextStyle]'s using it
 * are [captionTextStyle], `h2`, `h6`, `body2`, and `overline`
 */
private val regular = Font(R.font.raleway_regular, FontWeight.W400)

/**
 * This [Font] is used when [FontWeight.Medium] `fontWeight` is specified. The [TextStyle]'s using it
 * are `subtitle1`, and `caption`.
 */
private val medium = Font(R.font.raleway_medium, FontWeight.W500)

/**
 * This [Font] is used when [FontWeight.SemiBold] `fontWeight` is specified. The [TextStyle]'s using
 * it are `h3`, `h4`, `h5`, `subtitle2`, `body1`, and `button`.
 */
private val semibold = Font(R.font.raleway_semibold, FontWeight.W600)

/**
 * This is the [FontFamily] that is used by all of the [TextStyle]'s used by our app.
 */
private val craneFontFamily = FontFamily(fonts = listOf(light, regular, medium, semibold))

/**
 * [CraneBaseUserInput] and [CraneEditableUserInput] use copies of this [TextStyle] with the `color`
 * of the [Font] changed according to their needs. The resource ID of the [Font] that is used it
 * [R.font.raleway_regular].
 */
val captionTextStyle = TextStyle(
    fontFamily = craneFontFamily,
    fontWeight = FontWeight.W400,
    fontSize = 16.sp
)

/**
 * This is the custom [Typography] that is used by our [CraneTheme] custom [MaterialTheme]. The
 * [TextStyle]'s in it are used by accessing their field name from the [MaterialTheme.typography]
 * instance of [Typography] (ie. `MaterialTheme.typography.body1` will fetch the [TextStyle] that
 * is specified for [Typography.body1] by [craneTypography]). The arguments to the [Typography]
 * constructor are:
 *  - `defaultFontFamily` - the default [FontFamily] to be used for [TextStyle]'s provided in this
 *  constructor. This default will be used if the [FontFamily] on the [TextStyle] is null.
 *  - `h1` - is the largest headline, reserved for short, important text or numerals.
 *  - `h2` - is the second largest headline, reserved for short, important text or numerals.
 *  - `h3` - is the third largest headline, reserved for short, important text or numerals.
 *  - `h4` - is the fourth largest headline, reserved for short, important text or numerals.
 *  - `h5` - is the fifth largest headline, reserved for short, important text or numerals.
 *  - `h6` - is the sixth largest headline, reserved for short, important text or numerals.
 *  - `subtitle1` - is the largest subtitle, and is typically reserved for medium-emphasis text that
 *  is shorter in length.
 *  - `subtitle2` - is the smallest subtitle, and is typically reserved for medium-emphasis text
 *  that is shorter in length.
 *  - `body1` - is the largest body, and is typically used for long-form writing as it works well
 *  for small text sizes.
 *  - `body2` - is the smallest body, and is typically used for long-form writing as it works well
 *  for small text sizes.
 *  - `button` - text is a call to action used in different types of buttons (such as text, outlined
 *  and contained buttons) and in tabs, dialogs, and cards.
 *  - `caption` - is one of the smallest font sizes. It is used sparingly to annotate imagery or to
 *  introduce a headline.
 *  - `overline` - is one of the smallest font sizes. It is used sparingly to annotate imagery or to
 *  introduce a headline.
 */
val craneTypography = Typography(
    h1 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W300,
        fontSize = 96.sp
    ),
    h2 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 60.sp
    ),
    h3 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 48.sp
    ),
    h4 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 34.sp
    ),
    h5 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 24.sp
    ),
    h6 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 20.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp
    ),
    body1 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp
    ),
    button = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp
    ),
    overline = TextStyle(
        fontFamily = craneFontFamily,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp
    )
)
