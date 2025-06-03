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

package com.example.owl.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

val yellow200: Color = Color(0xffffeb46)
val yellow400: Color = Color(0xffffc000)
val yellow500: Color = Color(0xffffde03)
val yellowDarkPrimary: Color = Color(0xff242316)

val blue200: Color = Color(0xff91a4fc)
val blue700: Color = Color(0xff0336ff)
val blue800: Color = Color(0xff0035c9)
val blueDarkPrimary: Color = Color(0xff1c1d24)

val pink200: Color = Color(0xffff7597)
val pink500: Color = Color(0xffff0266)
val pink600: Color = Color(0xffd8004d)
val pinkDarkPrimary: Color = Color(0xff24191c)

/**
 * Calculates a color that represents the layering of the [Colors.onSurface] color with the
 * given [alpha] on top of the [Colors.surface] color. This is useful for achieving a
 * semi-transparent effect on surfaces, while ensuring the resulting color is opaque.
 *
 * For example, if you want to display text on a surface with a certain level of transparency,
 * you can use this function to calculate the color that should be used for the text.
 *
 * @param alpha The alpha value to use for the [Colors.onSurface] color. This should be a
 * value between 0.0 and 1.0, where 0.0 is completely transparent and 1.0 is completely
 * opaque.
 * @return The resulting opaque color.
 */
@Composable
fun Colors.compositedOnSurface(alpha: Float): Color {
    return onSurface.copy(alpha = alpha).compositeOver(background = surface)
}
