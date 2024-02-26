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

package com.example.compose.jetchat.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.compose.jetchat.R

/**
 * We use this as the Attributes used to create our FontRequests for [GoogleFont] based [Font]'s
 *  - `providerAuthority`: The authority of the Font Provider to be used for the request.
 *  - `providerPackage`: The package for the Font Provider to be used for the request. This is used
 *  to verify the identity of the provider
 *  - `certificates`: A resource array with the list of sets of hashes for the certificates the
 *  provider should be signed with. This is used to verify the identity of the provider. Each set
 *  in the list represents one collection of signature hashes. Refer to your font provider's
 *  documentation for these values.
 */
val provider: GoogleFont.Provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

/**
 * The "Montserrat" [GoogleFont] that we use for the [FontFamily] we create for [MontserratFontFamily].
 * For sample see: [Montserrat Sample](https://fonts.google.com/?query=Montserrat).
 */
val MontserratFont: GoogleFont = GoogleFont(name = "Montserrat")

/**
 * The "Karla" [GoogleFont] that we use for the [FontFamily] we create for [KarlaFontFamily].
 * For sample see: [Karla Sample](https://fonts.google.com/?query=Karla).
 */
val KarlaFont: GoogleFont = GoogleFont(name = "Karla")

/**
 * This is the [FontFamily] that we create from [MontserratFont]. Note that is uses both downloadable
 * fonts and local R.font resource folder fonts. Defining the [FontFamily] like this creates a [FontFamily]
 * containing two chains, one per weight. The loading mechanism will try to resolve the online font
 * first, and then the font located in our local R.font resource folder. Used for [TextStyle]'s:
 *  - [Typography.displayLarge] with a `fontWeight` of [FontWeight.Light] and a `fontSize` of 57.sp
 *  - [Typography.displayMedium] with a `fontWeight` of [FontWeight.Light] and a `fontSize` of 45.sp
 *  - [Typography.displaySmall] with a `fontWeight` of [FontWeight.Normal] and a `fontSize` of 36.sp
 *  - [Typography.headlineLarge] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize` of 32.sp
 *  - [Typography.headlineMedium] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize` of 28.sp
 *  - [Typography.headlineSmall] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize` of 24.sp
 *  - [Typography.titleLarge] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize` of 22.sp
 *  - [Typography.titleMedium] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize` of 16.sp
 *  - [Typography.bodyMedium] with a `fontWeight` of [FontWeight.Medium] and a `fontSize` of 14.sp
 *  - [Typography.labelLarge] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize` of 14.sp
 *  - [Typography.labelMedium] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize` of 12.sp
 *  - [Typography.labelSmall] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize` of 11.sp
 */
val MontserratFontFamily: FontFamily = FontFamily(
    Font(googleFont = MontserratFont, fontProvider = provider),
    Font(resId = R.font.montserrat_regular),
    Font(googleFont = MontserratFont, fontProvider = provider, weight = FontWeight.Light),
    Font(resId = R.font.montserrat_light, weight = FontWeight.Light),
    Font(googleFont = MontserratFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(resId = R.font.montserrat_medium, weight = FontWeight.Medium),
    Font(googleFont = MontserratFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(resId = R.font.montserrat_semibold, weight = FontWeight.SemiBold),
)

/**
 * This is the [FontFamily] that we create from [KarlaFont]. Note that is uses both downloadable
 * fonts and local R.font resource folder fonts. Defining the [FontFamily] like this creates a
 * [FontFamily] containing two chains, one per weight. The loading mechanism will try to resolve
 * the online font first, and then the font located in our local R.font resource folder.
 * Used for [TextStyle]'s:
 *  - [Typography.titleSmall] with a `fontWeight` of [FontWeight.Bold] and a `fontSize` of 14.sp
 *  - [Typography.bodyLarge] with a `fontWeight` of [FontWeight.Normal] and a `fontSize` of 16.sp
 *  - [Typography.bodySmall] with a `fontWeight` of [FontWeight.Bold] and a `fontSize` of 12.sp
 */
val KarlaFontFamily: FontFamily = FontFamily(
    Font(googleFont = KarlaFont, fontProvider = provider),
    Font(resId = R.font.karla_regular),
    Font(googleFont = KarlaFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(resId = R.font.karla_bold, weight = FontWeight.Bold),
)

/**
 * This is our custom [Typography]. The [TextStyle]'s we override are:
 *
 *  - [Typography.displayLarge] displayLarge is the largest display text, we use [MontserratFontFamily]
 *  with a `fontWeight` of [FontWeight.Light] and a `fontSize` of 57.sp
 *  - [Typography.displayMedium] displayMedium is the second largest display text, we use [MontserratFontFamily]
 *  with a `fontWeight` of [FontWeight.Light] and a `fontSize` of 45.sp
 *  - [Typography.displaySmall] displaySmall is the smallest display text, we use [MontserratFontFamily]
 *  with a `fontWeight` of [FontWeight.Normal] and a `fontSize` of 36.sp
 *  - [Typography.headlineLarge] headlineLarge is the largest headline, reserved for short, important
 *  text or numerals. For headlines, you can choose an expressive font, such as a display, handwritten,
 *  or script style. These unconventional font designs have details and intricacy that help attract
 *  the eye, we use [MontserratFontFamily] with a `fontWeight` of [FontWeight.SemiBold] and a
 *  `fontSize` of 32.sp
 *  - [Typography.headlineMedium] headlineMedium is the second largest headline, reserved for short,
 *  important text or numerals. For headlines, you can choose an expressive font, such as a display,
 *  handwritten, or script style. These unconventional font designs have details and intricacy that
 *  help attract the eye, we use [MontserratFontFamily] with a `fontWeight` of [FontWeight.SemiBold]
 *  and a `fontSize` of 28.sp
 *  - [Typography.headlineSmall] headlineSmall is the smallest headline, reserved for short, important
 *  text or numerals. For headlines, you can choose an expressive font, such as a display, handwritten,
 *  or script style. These unconventional font designs have details and intricacy that help attract
 *  the eye. We use [MontserratFontFamily] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize`
 *  of 24.sp
 *  - [Typography.titleLarge] titleLarge is the largest title, and is typically reserved for medium
 *  emphasis text that is shorter in length. Serif or sans serif typefaces work well for subtitles.
 *  We use [MontserratFontFamily] with a `fontWeight` of [FontWeight.SemiBold] and a `fontSize` of 22.sp
 *  - [Typography.titleMedium] titleMedium is the second largest title, and is typically reserved for
 *  medium-emphasis text that is shorter in length. Serif or sans serif typefaces work well for
 *  subtitles. We use [MontserratFontFamily] with a `fontWeight` of [FontWeight.SemiBold] and a
 *  `fontSize` of 16.sp
 *  - [Typography.titleSmall] titleSmall is the smallest title, and is typically reserved for medium
 *  emphasis text that is shorter in length. Serif or sans serif typefaces work well for subtitles.
 *  We use [KarlaFontFamily] with a `fontWeight` of [FontWeight.Bold] and a `fontSize` of 14.sp
 *  - [Typography.bodyLarge] bodyLarge is the largest body, and is typically used for long-form
 *  writing as it works well for small text sizes. For longer sections of text, a serif or sans serif
 *  typeface is recommended. We use [KarlaFontFamily] with a `fontWeight` of [FontWeight.Normal] and
 *  a `fontSize` of 16.sp
 *  - [Typography.bodyMedium] bodyMedium is the second largest body, and is typically used for long
 *  form writing as it works well for small text sizes. For longer sections of text, a serif or sans
 *  serif typeface is recommended. We use [MontserratFontFamily] with a `fontWeight` of
 *  [FontWeight.Medium] and a `fontSize` of 14.sp
 *  - [Typography.bodySmall] bodySmall is the smallest body, and is typically used for long-form
 *  writing as it works well for small text sizes. For longer sections of text, a serif or sans
 *  serif typeface is recommended. We use [KarlaFontFamily] with a `fontWeight` of [FontWeight.Bold]
 *  and a `fontSize` of 12.sp
 *  - [Typography.labelLarge] labelLarge text is a call to action used in different types of buttons
 *  (such as text, outlined and contained buttons) and in tabs, dialogs, and cards. Button text is
 *  typically sans serif, using all caps text. We use [MontserratFontFamily] with a `fontWeight` of
 *  [FontWeight.SemiBold] and a `fontSize` of 14.sp
 *  - [Typography.labelMedium] labelMedium is one of the smallest font sizes. It is used sparingly
 *  to annotate imagery or to introduce a headline. We use [MontserratFontFamily] with a `fontWeight`
 *  of [FontWeight.SemiBold] and a `fontSize` of 12.sp
 *  - [Typography.labelSmall] labelSmall is one of the smallest font sizes. It is used sparingly to
 *  annotate imagery or to introduce a headline. We use [MontserratFontFamily] with a `fontWeight`
 *  of [FontWeight.SemiBold] and a `fontSize` of 11.sp
 */
val JetchatTypography: Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = KarlaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = KarlaFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = KarlaFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
