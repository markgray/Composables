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

package com.example.compose.jetsurvey.survey.question

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.QuestionWrapper

/**
 * Composable that displays a multiple-choice question. Our root composable is a [QuestionWrapper]
 * whose arguments are:
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *  - `titleResourceId`: is our [Int] parameter [titleResourceId].
 *  - `directionsResourceId`: is our [Int] parameter [directionsResourceId].
 *
 * Inside of the `content` composable lambda argument of the [QuestionWrapper] we use the
 * [Iterable.forEach] method of our [List] of [Int] parameter [possibleAnswers] to loop through each
 * [Int] value capturing the [Int] passed the `action` lambda argument in our variable
 * `answerStringResId`. We then initialize our [Boolean] variable `selected` with the result of
 * calling the [List.contains] method of our [List] of [Int] parameter [selectedAnswers] with our
 * [Int] variable `answerStringResId` (returns `true` if the [Int] value is contained in the
 * [List] of [Int] parameter [selectedAnswers]). Then we compose a [CheckboxRow] whose arguments are:
 *  - `modifier`: is a [Modifier.padding] that adds `8.dp` padding to the vertical sides.
 *  - `text`: is the [String] whose resource ID is our [Int] variable `answerStringResId`.
 *  - `selected`: is our [Boolean] variable `selected`.
 *  - `onOptionSelected`: is a lambda that calls our lambda parameter [onOptionSelected] with the
 *  inverse of our [Boolean] variable `selected` and our [Int] variable `answerStringResId`.
 *
 * @param titleResourceId The string resource ID for the question title.
 * @param directionsResourceId The string resource ID for the question directions.
 * @param possibleAnswers A list of string resource IDs for the possible answers.
 * @param selectedAnswers A list of string resource IDs for the currently selected answers.
 * @param onOptionSelected A callback that is invoked when an answer is selected or deselected.
 * @param modifier The modifier to be applied to the composable.
 */
@Composable
fun MultipleChoiceQuestion(
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    possibleAnswers: List<Int>,
    selectedAnswers: List<Int>,
    onOptionSelected: (selected: Boolean, answer: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        modifier = modifier,
        titleResourceId = titleResourceId,
        directionsResourceId = directionsResourceId,
    ) {
        possibleAnswers.forEach { answerStringResId: Int ->
            val selected: Boolean = selectedAnswers.contains(answerStringResId)
            CheckboxRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(id = answerStringResId),
                selected = selected,
                onOptionSelected = { onOptionSelected(!selected, answerStringResId) }
            )
        }
    }
}

/**
 * A row composable that displays a checkbox and a text label. Our root composable is a [Surface]
 * whose arguments are:
 *  - `shape`: is the [Shapes.small] of our custom [MaterialTheme.shapes].
 *  - `color`: is the [ColorScheme.primaryContainer] of our custom [MaterialTheme.colorScheme] if
 *  `selected` is `true`, or the [ColorScheme.surface] of our custom [MaterialTheme.colorScheme]
 *  if it is `false`.
 *  - `border`: is a [BorderStroke] with its `width` set to `1.dp` and its `color` set to the
 *  [ColorScheme.primary] of our custom [MaterialTheme.colorScheme] if `selected` is `true`, or the
 *  [ColorScheme.outline] of our custom [MaterialTheme.colorScheme] if it is `false`.
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.clip] whose `shape` is
 *  the [Shapes.small] of our custom [MaterialTheme.shapes], with a [Modifier.clickable] whose
 *  `onClick` is our lambda parameter [onOptionSelected] chained to that.
 *
 * In the `content` composable lambda argument of the [Surface] we compose a [Row] whose arguments
 * are:
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds `16.dp` to
 *  all sides.
 *  - `verticalAlignment`: is [Alignment.CenterVertically].
 *
 * In the [RowScope] `content` composable lambda argument of the [Row] we compose:
 *
 * **First** a [Text] whose arguments are:
 *  - `text`: is our [String] parameter [text].
 *  - `modifier`: is a [RowScope.weight] with a weight of `1f`.
 *  - `style`: is the [Typography.bodyLarge] of our custom [MaterialTheme.typography].
 *
 * **Second** a [Box] whose `modifier` argument is a [Modifier.padding] that adds `8.dp` to all sides.
 * And in its [BoxScope] `content` composable lambda argument we compose a [Checkbox] whose arguments
 * are:
 *  - `checked`: is our [Boolean] parameter [selected].
 *  - `onCheckedChange`: is `null`.
 *
 * @param text The text to display next to the checkbox.
 * @param selected Whether the checkbox is currently selected.
 * @param onOptionSelected A callback that is invoked when the checkbox is clicked.
 * @param modifier The modifier to be applied to the composable.
 */
@Composable
fun CheckboxRow(
    text: String,
    selected: Boolean,
    onOptionSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.small)
            .clickable(onClick = onOptionSelected)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            Box(modifier = Modifier.padding(all = 8.dp)) {
                Checkbox(checked = selected, onCheckedChange = null)
            }
        }
    }
}

/**
 * Preview of a [MultipleChoiceQuestion].
 */
@Preview
@Composable
fun MultipleChoiceQuestionPreview() {
    val possibleAnswers = listOf(R.string.read, R.string.work_out, R.string.draw)
    val selectedAnswers = remember { mutableStateListOf(R.string.work_out) }
    MultipleChoiceQuestion(
        titleResourceId = R.string.in_my_free_time,
        directionsResourceId = R.string.select_all,
        possibleAnswers = possibleAnswers,
        selectedAnswers = selectedAnswers,
        onOptionSelected = { _, _ -> }
    )
}
