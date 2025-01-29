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

package com.example.reply.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.reply.R
import com.example.reply.ui.theme.md_theme_dark_outline
import com.example.reply.ui.theme.md_theme_dark_primary
import com.example.reply.ui.theme.md_theme_light_outline
import com.example.reply.ui.theme.md_theme_light_primary

/**
 * This Composable is displayed when the user tries to use a feature which has not been implemented.
 * It consists of a root [Column] holding two [Text] widgets. The `modifier` argument chains a
 * [Modifier.fillMaxSize] to our [Modifier] parameter [modifier] to have its `content` occupy the
 * entire incoming width constraints, its `verticalArrangement` argument is an [Arrangement.Center]
 * to center its children vertically, and its horizontalAlignment is [Alignment.CenterHorizontally]
 * to center the horizontal alignment of the layout's children. Its `content` is:
 *  - a [Text] displaying the `text` with resource ID `R.string.empty_screen_title` ("Screen under
 *  construction"), with a [Modifier.padding] that adds 8.dp to all its sides, its `style` uses the
 *  [TextStyle] that is defined for [Typography.titleLarge] by our custom [MaterialTheme], which is
 *  the [FontWeight.SemiBold] `fontWeight` with a `fontSize` of 18.sp, `lineHeight` of 32.sp, and
 *  `letterSpacing` of 0.sp. The `textAlign` of the [Text] is [TextAlign.Center] so the alignment of
 *  the text within the lines of the paragraph is centered, and the `color` of the text is the
 *  [ColorScheme.primary] of our custom [MaterialTheme] which is [md_theme_light_primary] for our
 *  [lightColorScheme] and [md_theme_dark_primary] for our [darkColorScheme].
 *
 *  - The second [Text] displays the `text` with resource ID `R.string.empty_screen_subtitle` ("This
 *  screen is still under construction), with a [Modifier.padding] that adds 8.dp along the left and
 *  right edges of its content, its `style` uses the [TextStyle] that is defined for
 *  [Typography.bodySmall] by our custom [MaterialTheme], which is the default defined by the system
 *  since we do not modify it. The `textAlign` of the [Text] is [TextAlign.Center] so the alignment
 *  of the text within the lines of the paragraph is centered, and the `color` of the text is the
 *  [ColorScheme.outline] of our custom [MaterialTheme] which is [md_theme_light_outline] for our
 *  [lightColorScheme] and [md_theme_dark_outline] for our [darkColorScheme].
 *
 * @param modifier a [Modifier] instance that our caller can use to modify our appearance and/or
 * behavior. Our [ReplyAppContent] caller passes us a [ColumnScope] `Modifier.weight` of 1f which
 * causes us to occupy all the remaining space in the incoming vertical constaint after our siblings
 * have been measured and placed.
 */
@Composable
fun EmptyComingSoon(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = stringResource(id = R.string.empty_screen_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = stringResource(id = R.string.empty_screen_subtitle),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

/**
 * Preview of [EmptyComingSoon]
 */
@Preview
@Composable
fun ComingSoonPreview() {
    EmptyComingSoon()
}
