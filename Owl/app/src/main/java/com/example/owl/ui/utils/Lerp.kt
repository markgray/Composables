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

package com.example.owl.ui.utils

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp as lerpColor

/**
 * Linearly interpolate between two [Float] values.
 *
 * @param startValue The starting value.
 * @param endValue The ending value.
 * @param fraction The fraction to interpolate by, between 0.0 and 1.0 (inclusive).
 * @return The interpolated value.
 */
fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): Float {
    return startValue + fraction * (endValue - startValue)
}

/**
 * Linearly interpolate between two [Float]s when the [fraction] is in a given range.
 *
 * When [fraction] is not within the range of [startFraction] to [endFraction], then [startValue]
 * or [endValue] will be returned respectively.
 *
 * @param startValue the starting value
 * @param endValue the ending value
 * @param startFraction the fraction at which to start the interpolation
 * @param endFraction the fraction at which to end the interpolation
 * @param fraction the current fraction
 * @return the interpolated value
 */
fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, to = 1.0) startFraction: Float,
    @FloatRange(from = 0.0, to = 1.0) endFraction: Float,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): Float {
    if (fraction < startFraction) return startValue
    if (fraction > endFraction) return endValue

    return lerp(
        startValue = startValue,
        endValue = endValue,
        fraction = (fraction - startFraction) / (endFraction - startFraction)
    )
}

/**
 * Linearly interpolate between two [Color]s when the [fraction] is in a given range.
 *
 * When [fraction] is not within the range of [startFraction] to [endFraction], then [startColor]
 * or [endColor] will be returned respectively.
 *
 * @param startColor the starting color
 * @param endColor the ending color
 * @param startFraction the fraction at which to start the interpolation
 * @param endFraction the fraction at which to end the interpolation
 * @param fraction the current fraction
 * @return the interpolated color
 */
fun lerp(
    startColor: Color,
    endColor: Color,
    @FloatRange(from = 0.0, to = 1.0) startFraction: Float,
    @FloatRange(from = 0.0, to = 1.0) endFraction: Float,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): Color {
    if (fraction < startFraction) return startColor
    if (fraction > endFraction) return endColor

    return lerpColor(
        start = startColor,
        stop = endColor,
        fraction = (fraction - startFraction) / (endFraction - startFraction)
    )
}
