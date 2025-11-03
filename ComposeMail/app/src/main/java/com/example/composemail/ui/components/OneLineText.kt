/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.composemail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A [Text] Composable that is constrained to a single line.
 *
 * This composable is a wrapper around [Text] that sets `maxLines` to 1. By default, it uses
 * [TextOverflow.Clip] for `overflow`, which is more performant than the default
 * [TextOverflow.Ellipsis] used by [Text], especially in animations.
 *
 * @param text The text to be displayed.
 * @param modifier The [Modifier] to be applied to this layout node.
 * @param color [Color] to apply to the text. If [Color.Unspecified], and [style] has no color set,
 * this will be [LocalContentColor].
 * @param style Style configuration for the text such as font, letter spacing, and line height.
 * @param overflow How visual overflow should be handled.
 */
@Composable
fun OneLineText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    overflow: TextOverflow = TextOverflow.Clip
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = style,
        maxLines = 1,
        overflow = overflow,
    )
}

/**
 * A preview of the [OneLineText] Composable compared to the behavior of a regular [Text]
 * Composable.
 */
@Preview
@Composable
private fun OneLineTextPreview() {
    Column(Modifier.fillMaxSize()) {
        Text(text = "Normal")
        Column(
            modifier = Modifier
                .width(width = 40.dp)
                .background(color = Color.LightGray)
        ) {
            Text(text = "Hello \nWorld!")
            Text(text = "This is a very very long text")
        }
        Text(text = "Cheap")
        Column(
            modifier = Modifier
                .width(width = 40.dp)
                .background(color = Color.LightGray)
        ) {
            OneLineText(text = "Hello \nWorld!")
            OneLineText(text = "This is a very very long text")
        }
    }
}