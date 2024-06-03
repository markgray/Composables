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

package com.example.jetnews.ui.interests

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetnews.ui.theme.JetnewsTheme

/**
 * Used by [TopicItem] to indicate whether the topic it displays is "selected" or not. We start by
 * initializing our [ImageVector] variable `val icon` to the [ImageVector] drawn by [Icons.Filled.Done]
 * if our [Boolean] parameter [selected] is `true` (a check-mark character), or to the [ImageVector]
 * drawn by [Icons.Filled.Add] if `false` (a "+" character). We initializxe our [Color] variable
 * `val iconColor` to the [ColorScheme.onPrimary] of our custom [MaterialTheme.colorScheme] if
 * [selected] is `true` or to the [ColorScheme.primary] if it is `false`. We initialize our [Color]
 * variable `val borderColor` to the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme]
 * if [selected] is `true` or the a copy of the [ColorScheme.onSurface] with an `alpha` of 0.1f if
 * it is `false`. We initialize our [Color] variable `val backgroundColor` to the [ColorScheme.primary]
 * of our custom [MaterialTheme.colorScheme] if [selected] is `true` or to the [ColorScheme.onPrimary]
 * if it is `false`.
 *
 * Our root Composable is a [Surface] whose `color` argument is our [Color] variable `backgroundColor`,
 * whose [Shape] `shape` argument is [CircleShape], whose `border` argument is a [BorderStroke] with
 * `width` of 1.dp and `color` our [Color] variable `borderColor`, and whose `modifier` argument is
 * chains a [Modifier.size] to our [Modifier] parameter [modifier] that sets its `width` to 36.dp
 * and its `height` to 36.dp. In its `content` lambda argument it composes an [Image] whose `imageVector`
 * argument is our [ImageVector] variable `icon`, whose `colorFilter` argument is a [ColorFilter.tint]
 * with `color` our [Color] variable `iconColor`, and the `modifier` argument of the [Image] is a
 * [Modifier.padding] that adds 8.dp to all of its sides.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our caller [TopicItem] passes none so the empty, default, or starter [Modifier] that
 * contains no elements is used.
 * @param selected if `true` the [TopicItem] that we are decorating is currently "selected" and our
 * appearance needs to reflect this fact.
 */
@Composable
fun SelectTopicButton(
    modifier: Modifier = Modifier,
    selected: Boolean = false
) {
    val icon: ImageVector = if (selected) Icons.Filled.Done else Icons.Filled.Add
    val iconColor: Color = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }
    val borderColor: Color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    }
    val backgroundColor: Color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        border = BorderStroke(width = 1.dp, color = borderColor),
        modifier = modifier.size(width = 36.dp, height = 36.dp)
    ) {
        Image(
            imageVector = icon,
            colorFilter = ColorFilter.tint(color = iconColor),
            modifier = Modifier.padding(all = 8.dp),
            contentDescription = null // toggleable at higher level
        )
    }
}

/**
 * Previews of [SelectTopicButtonPreviewTemplate] with its `selected` argument `false`
 */
@Preview(name = "Off")
@Preview(name = "Off (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SelectTopicButtonPreviewOff() {
    SelectTopicButtonPreviewTemplate(
        selected = false
    )
}

/**
 * Previews of [SelectTopicButtonPreviewTemplate] with its `selected` argument `true`
 */
@Preview(name = "On")
@Preview(name = "On (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SelectTopicButtonPreviewOn() {
    SelectTopicButtonPreviewTemplate(
        selected = true
    )
}

/**
 * Our Previews use this to preview [SelectTopicButton] with different values of its [Boolean]
 * argument [selected] but the same `modifier` argument, a [Modifier.padding] that adds 32.dp
 * to all sides.
 *
 * @param selected the value to use for the `selected` argument of [SelectTopicButton].
 */
@Composable
private fun SelectTopicButtonPreviewTemplate(
    selected: Boolean
) {
    JetnewsTheme {
        Surface {
            SelectTopicButton(
                modifier = Modifier.padding(all = 32.dp),
                selected = selected
            )
        }
    }
}
