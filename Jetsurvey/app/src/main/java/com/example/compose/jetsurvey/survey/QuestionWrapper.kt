/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.compose.jetsurvey.survey

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.theme.slightlyDeemphasizedAlpha
import com.example.compose.jetsurvey.theme.stronglyDeemphasizedAlpha

/**
 * A scrollable container with the question's title, direction, and dynamic content.
 *
 * Our root composable is a [Column] whose `modifier` argument chains [Modifier.padding] to our
 * [Modifier] parameter` [modifier] that adds `16.dp` to the `horizontal` sides, with a
 * [Modifier.verticalScroll] chained to that which allows it to scroll vertically. Inside its
 * [ColumnScope] `content` composable lambda argument we compose:
 *
 * **First** a [Spacer] whose `modifier` argument is a [Modifier.height] with a height of `32.dp`
 *
 * **Second** a [QuestionTitle] whose `title` argument is our [Int] parameter [titleResourceId].
 *
 * **Third** if our [Int] parameter [directionsResourceId] is not `null` we compose a [Spacer] whose
 * `modifier` argument is a [Modifier.height] with a height of `18.dp` and a [QuestionDirections]
 * whose `directionsResourceId` argument is our [Int] parameter [directionsResourceId].
 *
 * **Fourth** a [Spacer] whose `modifier` argument is a [Modifier.height] with a height of `18.dp`
 *
 * **Fifth** our [content] composable lambda argument.
 *
 * @param titleResourceId String resource to use for the question's title
 * @param modifier Modifier to apply to the entire wrapper
 * @param directionsResourceId String resource to use for the question's directions; the direction
 * UI will be omitted if null is passed
 * @param content Composable to display below the title and question directions in our scrollable
 * [Column].
 */
@Composable
fun QuestionWrapper(
    @StringRes titleResourceId: Int,
    modifier: Modifier = Modifier,
    @StringRes directionsResourceId: Int? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(height = 32.dp))
        QuestionTitle(title = titleResourceId)
        directionsResourceId?.let { direction: Int ->
            Spacer(modifier = Modifier.height(height = 18.dp))
            QuestionDirections(directionsResourceId = direction)
        }
        Spacer(modifier = Modifier.height(height = 18.dp))

        content()
    }
}

/**
 * Displays the title of a question. Our root composable is a [Text] whose arguments are:
 *  - `text`: is the [String] whose resource ID is our [Int] parameter [title].
 *  - `style`: is the [Typography.titleMedium] of our custom [MaterialTheme.typography].
 *  - `color`: is the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme]
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.fillMaxWidth] chained to
 *  a [Modifier.background] whose `color` argument is the [ColorScheme.inverseOnSurface] of our
 *  custom [MaterialTheme.colorScheme] and whose `shape` argument is the [Shapes.small] of our
 *  custom [MaterialTheme.shapes], with a [Modifier.padding] of that adds `24.dp` to the `vertical`
 *  sides and `16.dp` to the `horizontal` sides.
 *
 * @param title The string resource id of the question's title.
 * @param modifier A [Modifier] to be applied to the layout.
 */
@Composable
private fun QuestionTitle(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(id = title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = slightlyDeemphasizedAlpha),
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.inverseOnSurface,
                shape = MaterialTheme.shapes.small
            )
            .padding(vertical = 24.dp, horizontal = 16.dp)
    )
}

/**
 * Displays the directions for a question. Our root composable is a [Text] whose arguments are:
 *  - `text`: is the [String] whose resource ID is our [Int] parameter [directionsResourceId].
 *  - `color`: is a copy of the [ColorScheme.onSurface] of our custom [MaterialTheme.colorScheme]
 *  with its `alpha` property changed to the [stronglyDeemphasizedAlpha] constant (0.6f).
 *  - `style`: is the [Typography.bodySmall] of our custom [MaterialTheme.typography].
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.fillMaxWidth] that
 *  causes it to occupy its entire incoming width constraint, with a [Modifier.padding] chained to
 *  that which adds `8.dp` of padding to the horizontal sides.
 *
 * @param directionsResourceId The string resource id of the question's directions.
 * @param modifier A [Modifier] to be applied to the layout.
 */
@Composable
private fun QuestionDirections(
    @StringRes directionsResourceId: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(id = directionsResourceId),
        color = MaterialTheme.colorScheme.onSurface
            .copy(alpha = stronglyDeemphasizedAlpha),
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
}
