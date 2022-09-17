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

package com.codelab.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.layouts.ui.LayoutsCodelabTheme

/**
 * This Composable consists of a [Row] holding a 50.dp by 50.dp [Surface] at its left end and a
 * [Column] holding two [Text] widgets at its right end. The `modifier` argument of the [Row] adds
 * a [Modifier.padding] of 8.dp to the [Row], uses [Modifier.clip] to clip the [Row] using a
 * [RoundedCornerShape] of 4.dp, uses [Modifier.background] to set its background `color` to the
 * `surface` [Color] of [MaterialTheme.colors] ([Color.White]), uses [Modifier.clickable] to set
 * its `onClick` to an empty lambda, and finally uses [Modifier.padding] to add another 16.dp to
 * its padding. The [Row]'s children are:
 *  - a [Surface] whose `modifier` argument is a [Modifier.size] that sets its size to 50.dp by
 *  50.dp, whose `shape` is a [CircleShape] (Defines the surface's shape as well its shadow to be
 *  a Circular Shape with all the corners sized as the 50 percent of the shape size), and whose
 *  `color` argument is a copy of the `onSurface` [Color] of [MaterialTheme.colors] whose `alpha`
 *  is modified to be 0.2f
 *  - a [Column] whose `modifier` argument uses [Modifier.padding] to set its `start` padding to
 *  8.dp, and uses the `Modifier.align` method of `RowScope` to align the [Column] to be centered
 *  vertically within the [Row]. The children of the [Column] are a [Text] displaying the string
 *  "Alfred Sisley" using the [FontWeight.Bold] `fontWeight` and a [Text] displaying the string
 *  "3 minutes ago" using as its `style` [TextStyle] the `body2` [TextStyle] of [MaterialTheme.typography]
 *  (`fontWeight` = [FontWeight.Normal], `fontSize` = 14.sp, `letterSpacing` = 0.25.sp). The second
 *  [Text] is wrapped by a [CompositionLocalProvider] which maps requests of [LocalContentAlpha]
 *  (the preferred content alpha for a given position in the hierarchy) to be [ContentAlpha.medium]
 *  (A medium level of content alpha, used to represent medium emphasis text) making the text of
 *  this [Text] a shade of gray.
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance or behavior.
 * Our caller does not pass one so the empty, default, or starter [Modifier] that contains no elements
 * is used.
 */
@Composable
fun PhotographerCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(all = 8.dp)
            .clip(RoundedCornerShape(size = 4.dp))
            .background(color = MaterialTheme.colors.surface)
            .clickable(onClick = { /* Ignoring onClick */ })
            .padding(all = 16.dp)
    ) {
        Surface(
            modifier = Modifier.size(size = 50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {
            // Image goes here
        }
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(alignment = Alignment.CenterVertically)
        ) {
            Text(text = "Alfred Sisley", fontWeight = FontWeight.Bold)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = "3 minutes ago", style = MaterialTheme.typography.body2)
            }
        }
    }
}

/**
 * Preview of [PhotographerCard] wrapped in our [LayoutsCodelabTheme] custom [MaterialTheme].
 */
@Preview
@Composable
fun PhotographerCardPreview() {
    LayoutsCodelabTheme {
        PhotographerCard()
    }
}
