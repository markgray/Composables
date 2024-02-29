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

package com.example.jetlagged.ui.theme

import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.jetlagged.R
import com.example.jetlagged.JetLaggedHeader
import com.example.jetlagged.JetLaggedSleepSummary
import com.example.jetlagged.SleepTabText
import com.example.jetlagged.DayLabel
import com.example.jetlagged.HoursHeader
import com.example.jetlagged.LegendItem

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
 * The "Lato" [GoogleFont] that we use for the [FontFamily] we create for [fontFamily].
 * For sample see: [Lato Sample](https://fonts.google.com/?query=Lato).
 */
val fontName: GoogleFont = GoogleFont("Lato")

/**
 * This is the [FontFamily] that we create from [fontName]. It consists only the [Font] created from
 * the downloadable [GoogleFont] field [fontName].
 */
val fontFamily: FontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)

/**
 * Set of Material typography styles to start with. We only override the [Typography.bodyLarge] with
 * a [TextStyle] whose `fontFamily` is the [FontFamily.Default], whose `fontWeight` is [FontWeight.Normal],
 * and whose `fontSize` is 16.sp
 */
val Typography: Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

/**
 * Used as the `style` [TextStyle] argument of the [Text] used by the [JetLaggedHeader] Composable.
 * Its [TextStyle.fontSize] is 22.sp, its [TextStyle.fontWeight] is a [FontWeight] whose
 * [FontWeight.weight] is 700, and its [TextStyle.fontFamily] is our [FontFamily] field [fontFamily].
 */
val TitleBarStyle: TextStyle = TextStyle(
    fontSize = 22.sp,
    fontWeight = FontWeight(weight = 700),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily
)

/**
 *  Used as the `style` [TextStyle] argument of two [Text]'s used by the [JetLaggedSleepSummary]
 *  Composable. Its [TextStyle.fontSize] is 24.sp, its [TextStyle.fontWeight] is a [FontWeight] whose
 *  [FontWeight.weight] is 600, and its [TextStyle.fontFamily] is our [FontFamily] field [fontFamily].
 */
val HeadingStyle: TextStyle = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight(weight = 600),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily
)

/**
 * Used as the `style` [TextStyle] argument of [Text]'s used by the [JetLaggedSleepSummary] Composable,
 * [SleepTabText] Composable, [DayLabel] Composable, and [HoursHeader] Composable. Its [TextStyle.fontSize]
 * is 16.sp, its [TextStyle.fontWeight] is a [FontWeight] whose [FontWeight.weight] is 600, and its
 * [TextStyle.fontFamily] is our [FontFamily] field [fontFamily].
 */
val SmallHeadingStyle: TextStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight(weight = 600),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily
)

/**
 * Used as the `style` [TextStyle] argument of the [Text] used by the [LegendItem] Composable. Its
 * [TextStyle.fontSize] is 16.sp, its [TextStyle.fontWeight] is a [FontWeight] whose [FontWeight.weight]
 * is 600, and its [TextStyle.fontFamily] is our [FontFamily] field [fontFamily].
 */
val LegendHeadingStyle: TextStyle = TextStyle(
    fontSize = 10.sp,
    fontWeight = FontWeight(weight = 600),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily
)
