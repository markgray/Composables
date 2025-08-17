/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.feature.topic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

/**
 * A placeholder for the topic detail screen.
 * This is shown when no topic is selected.
 *
 * Our root composable is a [Card] whose arguments are:
 *  - `modifier`: our [Modifier] parameter [modifier].
 *  - `colors`: a [CardDefaults.cardColors] with its `containerColor` the [ColorScheme.surfaceVariant]
 *  of our custom [MaterialTheme.colorScheme].
 *  - `shape`: a [RoundedCornerShape] with its `topStart` `24.dp, `topEnd` `24.dp`, `bottomEnd` `0.dp`
 *  and `bottomStart` `0.dp`.
 *
 * In its [ColumnScope] `content` composable lambda argument we compose a [Column] whose arguments
 * are:
 *  - `modifier`: a [Modifier.fillMaxSize].
 *  - `horizontalAlignment`: [Alignment.CenterHorizontally].
 *  - `verticalArrangement`: an [Arrangement.spacedBy] whose `space` is `20.dp` and `alignment` is
 *  [Arrangement.Center].
 *
 * In the [ColumnScope] `content` composable lambda argument of the [Column] we first compose an
 * [Icon] whose arguments are:
 *  - `painter`: the [Painter] returned by the [painterResource] for the drawable with resource ID
 *  `R.drawable.feature_topic_ic_topic_placeholder`.
 *  - `contentDescription`: `null`.
 *  - `tint`: the [ColorScheme.primary] of our custom [MaterialTheme.colorScheme].
 *
 * Next we compose a [Text] whose arguments are:
 *  - `text`: the string with resource ID `R.string.feature_topic_select_an_interest`
 *  ("Select an Interest").
 *  - `style`: the [Typography.titleLarge] of our custom [MaterialTheme.typography].
 *
 * @param modifier The modifier to be applied to the placeholder.
 */
@Composable
fun TopicDetailPlaceholder(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                space = 20.dp,
                alignment = Alignment.CenterVertically,
            ),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.feature_topic_ic_topic_placeholder),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(id = R.string.feature_topic_select_an_interest),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

/**
 * A preview of the [TopicDetailPlaceholder] composable. It is wrapped in a [NiaTheme] and displays
 * the placeholder with a width of 200dp and height of 300dp.
 */
@Preview(widthDp = 200, heightDp = 300)
@Composable
fun TopicDetailPlaceholderPreview() {
    NiaTheme {
        TopicDetailPlaceholder()
    }
}
