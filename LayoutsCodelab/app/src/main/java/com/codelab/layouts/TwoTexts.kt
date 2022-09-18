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

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.layouts.ui.LayoutsCodelabTheme

/**
 * A Composable that consists of a [Row] root Composable holding a [Text], a 1.dp wide [Divider],
 * and a second [Text]. For its `modifier` argument the [Row] adds a [Modifier.height] to our
 * [modifier] parameter which declares the preferred height of its content to be the same as the min
 * intrinsic height of the content. The first [Text] uses as its `modifier` argument the
 * `Modifier.weight` [Modifier] of `RowScope` to set its weight to 1f (the other [Text] does so too
 * so they split evenly the space left after the [Divider] in their middle is measured), then adds
 * a [Modifier.padding] to add 4.dp padding to its start, and a [Modifier.wrapContentWidth] of
 * [Alignment.Start] which aligns its text to the start of the [Text]. The `text` argument of the
 * first [Text] is our [String] parameter [text1]. The next widget in the [Row] is a [Divider] whose
 * `color` argument sets the color of the dividing line to [Color.Black], and whose `modifier`
 * argument uses [Modifier.fillMaxHeight] to have it fill the height of the incoming measurement
 * constraint, to which is added a [Modifier.width] to set its width to 1.dp. Then comes the second
 * [Text] which uses as its `modifier` argument the `Modifier.weight` [Modifier] of `RowScope` to
 * set its weight to 1f (the other [Text] does so too so they split evenly the space left after the
 * [Divider] in their middle is measured), then adds a [Modifier.padding] to add 4.dp padding to its
 * end, and a [Modifier.wrapContentWidth] of [Alignment.End] which aligns its text to the end of the
 * [Text].  The `text` argument of the second [Text] is our [String] parameter [text2].
 *
 * @param modifier a [Modifier] instance which allows our caller to modify our appearance or behavior.
 * Our caller passes no value so the empty, default, or starter [Modifier] that contains no elements
 * is used.
 * @param text1 the text that the first [Text] in our [Row] should display.
 * @param text2 the text that the second [Text] in our [Row] should display.
 */
@Composable
fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
    Row(modifier = modifier.height(intrinsicSize = IntrinsicSize.Min)) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.Start),
            text = text1
        )
        Divider(color = Color.Black, modifier = Modifier.fillMaxHeight().width(width = 1.dp))
        Text(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(end = 4.dp)
                .wrapContentWidth(align = Alignment.End),
            text = text2
        )
    }
}

/**
 * The Preview of our [TwoTexts] Composable. Wrapped in our [LayoutsCodelabTheme] custom [MaterialTheme]
 * is a [Surface] whose `content` is a [TwoTexts] Composable whose `text1` argument is "Hi" and whose
 * `text2` argument is "there". (The "Hi" will be displayed by the first [Text] in the [Row] of [TwoTexts]
 * and the "there" will be displayed by the second [Text].
 */
@Preview
@Composable
fun TwoTextsPreview() {
    LayoutsCodelabTheme {
        Surface {
            TwoTexts(text1 = "Hi", text2 = "there")
        }
    }
}
