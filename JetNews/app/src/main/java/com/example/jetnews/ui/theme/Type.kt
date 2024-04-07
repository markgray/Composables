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

package com.example.jetnews.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.example.jetnews.R

/**
 * This is the [FontFamily] we use for our [TextStyle] field [defaultTextStyle], which is the basis
 * for the [TextStyle]'s used to create the [Typography] for our [JetnewsTheme] custom [MaterialTheme]
 */
private val Montserrat = FontFamily(
    Font(R.font.montserrat_regular),
    Font(R.font.montserrat_medium, FontWeight.W500)
)

/**
 * This is the basis for all the other [TextStyle], with its defaults overridden when necessary.
 */
val defaultTextStyle: TextStyle = TextStyle(
    fontFamily = Montserrat,
    platformStyle = PlatformTextStyle(
        includeFontPadding = false
    ),
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None
    )
)

/**
 * The custom [Typography] used by our [JetnewsTheme] custom [MaterialTheme].
 *
 *  - `displayLarge` is the largest display text. We override the [TextStyle.fontSize] of
 *  [defaultTextStyle] with 57.sp, the [TextStyle.lineHeight] with 64.sp, and the
 *  [TextStyle.letterSpacing] with (-0.25).sp
 *  - `displayMedium` is the second largest display text. We override the [TextStyle.fontSize] of
 *  [defaultTextStyle] with 45.sp, the [TextStyle.lineHeight] with 52.sp, and the
 *  [TextStyle.letterSpacing] with 0.sp
 *  - `displaySmall` is the smallest display text. We override the [TextStyle.fontSize] of
 *  [defaultTextStyle] with 36.sp, the [TextStyle.lineHeight] with 44.sp, and the
 *  [TextStyle.letterSpacing] with 0.sp
 *  - `headlineLarge` is the largest headline, reserved for short, important text or numerals. For
 *  headlines, you can choose an expressive font, such as a display, handwritten, or script style.
 *  These unconventional font designs have details and intricacy that help attract the eye. We
 *  override the [TextStyle.fontSize] of [defaultTextStyle] with 32.sp, the [TextStyle.lineHeight]
 *  with 40.sp, the [TextStyle.letterSpacing] with 0.sp, and the [TextStyle.lineBreak] with
 *  [LineBreak.Heading] (Balanced line lengths, hyphenation, and phrase-based breaking. Suitable for
 *  short text such as titles or narrow newspaper columns)
 *  - `headlineMedium` is the second largest headline, reserved for short, important text or numerals.
 *  For headlines, you can choose an expressive font, such as a display, handwritten, or script style.
 *  These unconventional font designs have details and intricacy that help attract the eye. We
 *  override the [TextStyle.fontSize] of [defaultTextStyle] with 28.sp, the [TextStyle.lineHeight]
 *  with 36.sp, the [TextStyle.letterSpacing] with 0.sp, and the [TextStyle.lineBreak] with
 *  [LineBreak.Heading] (Balanced line lengths, hyphenation, and phrase-based breaking. Suitable for
 *  short text such as titles or narrow newspaper columns).
 *  - `headlineSmall` is the smallest headline, reserved for short, important text or numerals. For
 *  headlines, you can choose an expressive font, such as a display, handwritten, or script style.
 *  These unconventional font designs have details and intricacy that help attract the eye. We
 *  override the [TextStyle.fontSize] of [defaultTextStyle] with 24.sp, the [TextStyle.lineHeight]
 *  with 32.sp, the [TextStyle.letterSpacing] with 0.sp, and the [TextStyle.lineBreak] with
 *  [LineBreak.Heading] (Balanced line lengths, hyphenation, and phrase-based breaking. Suitable for
 *  short text such as titles or narrow newspaper columns).
 *  - `titleLarge` is the largest title, and is typically reserved for medium-emphasis text that is
 *  shorter in length. Serif or sans serif typefaces work well for subtitles. We override the
 *  [TextStyle.fontSize] of [defaultTextStyle] with 22.sp, the [TextStyle.lineHeight] with 28.sp,
 *  the [TextStyle.letterSpacing] with 0.sp, and the [TextStyle.lineBreak] with [LineBreak.Heading]
 *  (Balanced line lengths, hyphenation, and phrase-based breaking. Suitable for short text such as
 *  titles or narrow newspaper columns).
 *  - `titleMedium` is the second largest title, and is typically reserved for medium-emphasis text
 *  that is shorter in length. Serif or sans serif typefaces work well for subtitles. We override
 *  the [TextStyle.fontSize] of [defaultTextStyle] with 16.sp, the [TextStyle.lineHeight] with 24.sp,
 *  the [TextStyle.letterSpacing] with 0.15.sp, the [TextStyle.fontWeight] with [FontWeight.Medium],
 *  and the [TextStyle.lineBreak] with [LineBreak.Heading] (Balanced line lengths, hyphenation, and
 *  phrase-based breaking. Suitable for short text such as titles or narrow newspaper columns).
 *  - `titleSmall` is the smallest title, and is typically reserved for medium-emphasis text that is
 *  shorter in length. Serif or sans serif typefaces work well for subtitles. We override the
 *  [TextStyle.fontSize] of [defaultTextStyle] with 14.sp, the [TextStyle.lineHeight] with 20.sp,
 *  the [TextStyle.letterSpacing] with 0.15.sp, the [TextStyle.fontWeight] with [FontWeight.Medium],
 *  and the [TextStyle.lineBreak] with [LineBreak.Heading] (Balanced line lengths, hyphenation, and
 *  phrase-based breaking. Suitable for short text such as titles or narrow newspaper columns).
 *  - `labelLarge` is a call to action used in different types of buttons (such as text, outlined
 *  and contained buttons) and in tabs, dialogs, and cards. Button text is typically sans serif,
 *  using all caps text. We override the [TextStyle.fontSize] of [defaultTextStyle] with 14.sp, the
 *  [TextStyle.lineHeight] with 20.sp, the [TextStyle.letterSpacing] with 0.1.sp, and the
 *  [TextStyle.fontWeight] with [FontWeight.Medium].
 *  - `labelMedium` is one of the smallest font sizes. It is used sparingly to annotate imagery or
 *  to introduce a headline. We override the [TextStyle.fontSize] of [defaultTextStyle] with 12.sp,
 *  the [TextStyle.lineHeight] with 20.sp, the [TextStyle.letterSpacing] with 0.5.sp, and the
 *  [TextStyle.fontWeight] with [FontWeight.Medium].
 *  - `labelSmall` is one of the smallest font sizes. It is used sparingly to annotate imagery or to
 *  introduce a headline. We override the [TextStyle.fontSize] of [defaultTextStyle] with 11.sp,
 *  [TextStyle.lineHeight] with 16.sp, the [TextStyle.letterSpacing] with 0.5.sp, and the
 *  [TextStyle.fontWeight] with [FontWeight.Medium].
 *  - `bodyLarge` is the largest body, and is typically used for long-form writing as it works well
 *  for small text sizes. For longer sections of text, a serif or sans serif typeface is recommended.
 *  We override the [TextStyle.fontSize] of [defaultTextStyle] with 16.sp, [TextStyle.lineHeight]
 *  with 24.sp, the [TextStyle.letterSpacing] with 0.5.sp, and the [TextStyle.lineBreak] with
 *  [LineBreak.Paragraph] (Slower, higher quality line breaking for improved readability. Suitable
 *  for larger amounts of text).
 *  - `bodyMedium` is the second largest body, and is typically used for long-form writing as it
 *  works well for small text sizes. For longer sections of text, a serif or sans serif typeface is
 *  recommended. We override the [TextStyle.fontSize] of [defaultTextStyle] with 14.sp,
 *  [TextStyle.lineHeight] with 20.sp, the [TextStyle.letterSpacing] with 0.25.sp, and the
 *  [TextStyle.lineBreak] with [LineBreak.Paragraph] (Slower, higher quality line breaking for
 *  improved readability. Suitable for larger amounts of text).
 *  - `bodySmall` is the smallest body, and is typically used for long-form writing as it works well
 *  for small text sizes. For longer sections of text, a serif or sans serif typeface is recommended.
 *  We override the [TextStyle.fontSize] of [defaultTextStyle] with 12.sp, [TextStyle.lineHeight]
 *  with 16.sp, the [TextStyle.letterSpacing] with 0.4.sp, and the [TextStyle.lineBreak] with
 *  [LineBreak.Paragraph] (Slower, higher quality line breaking for improved readability. Suitable
 *  for larger amounts of text).
 */
val JetnewsTypography: Typography = Typography(
    displayLarge = defaultTextStyle.copy(
        fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp
    ),
    displayMedium = defaultTextStyle.copy(
        fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp
    ),
    displaySmall = defaultTextStyle.copy(
        fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp
    ),
    headlineLarge = defaultTextStyle.copy(
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp, lineBreak = LineBreak.Heading
    ),
    headlineMedium = defaultTextStyle.copy(
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp, lineBreak = LineBreak.Heading
    ),
    headlineSmall = defaultTextStyle.copy(
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp, lineBreak = LineBreak.Heading
    ),
    titleLarge = defaultTextStyle.copy(
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp, lineBreak = LineBreak.Heading
    ),
    titleMedium = defaultTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
        fontWeight = FontWeight.Medium,
        lineBreak = LineBreak.Heading
    ),
    titleSmall = defaultTextStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        fontWeight = FontWeight.Medium,
        lineBreak = LineBreak.Heading
    ),
    labelLarge = defaultTextStyle.copy(
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp, fontWeight = FontWeight.Medium
    ),
    labelMedium = defaultTextStyle.copy(
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium
    ),
    labelSmall = defaultTextStyle.copy(
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium
    ),
    bodyLarge = defaultTextStyle.copy(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        lineBreak = LineBreak.Paragraph
    ),
    bodyMedium = defaultTextStyle.copy(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        lineBreak = LineBreak.Paragraph
    ),
    bodySmall = defaultTextStyle.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        lineBreak = LineBreak.Paragraph
    ),
)
