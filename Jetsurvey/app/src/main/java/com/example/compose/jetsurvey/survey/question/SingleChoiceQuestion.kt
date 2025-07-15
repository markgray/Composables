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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.QuestionWrapper

/**
 * Displays a single choice question with a list of possible answers. Our root composable is a
 * [QuestionWrapper] whose `titleResourceId` argument is our [Int] parameter [titleResourceId],
 * whose `directionsResourceId` argument is our [Int] parameter [directionsResourceId], and whose
 * `modifier` argument is our [Modifier] parameter [modifier]. Inside the `content` composable
 * lambda argument of the [QuestionWrapper] we use the [Iterable.forEach] method of the [List] of
 * [Superhero] parameter [possibleAnswers] to loop through the [List] capturing each [Superhero] in
 * variable `hero`. We initialize our [Boolean] variable `selected` with `true` if our [Superhero]
 * variable `hero` is the same as our [Superhero] parameter [selectedAnswer], or `false` otherwise.
 * We then compose a [RadioButtonWithImageRow] whose arguments are:
 *  - `modifier`: is a [Modifier.padding] that adds `8.dp` padding to each `vertical` side.
 *  - `text`: is the [String] whose resource ID is the [Superhero.stringResourceId] of `hero`.
 *  - `imageResourceId`: is the [Superhero.imageResourceId] property of `hero`.
 *  - `selected`: is our [Boolean] variable `selected`.
 *  - `onOptionSelected`: is a lambda that calls our [onOptionSelected] lambda parameter with `hero`.
 *
 * @param titleResourceId The string resource ID for the question title.
 * @param directionsResourceId The string resource ID for the question directions.
 * @param possibleAnswers A list of [Superhero] objects representing the possible answers.
 * @param selectedAnswer The currently selected [Superhero] answer, or null if no answer is selected.
 * @param onOptionSelected A callback function that is invoked when an answer is selected.
 * @param modifier The modifier to be applied to the composable.
 */
@Composable
fun SingleChoiceQuestion(
    @StringRes titleResourceId: Int,
    @StringRes directionsResourceId: Int,
    possibleAnswers: List<Superhero>,
    selectedAnswer: Superhero?,
    onOptionSelected: (Superhero) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuestionWrapper(
        titleResourceId = titleResourceId,
        directionsResourceId = directionsResourceId,
        modifier = modifier.selectableGroup(),
    ) {
        possibleAnswers.forEach { hero: Superhero ->
            val selected: Boolean = hero == selectedAnswer
            RadioButtonWithImageRow(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(id = hero.stringResourceId),
                imageResourceId = hero.imageResourceId,
                selected = selected,
                onOptionSelected = { onOptionSelected(hero) }
            )
        }
    }
}

/**
 * Composable that displays a radio button with an image and text. Our root composable is a [Surface]
 * whose argument are:
 *  - `shape`: is the [Shapes.small] of our custom [MaterialTheme.shapes].
 *  - `color`: is the [ColorScheme.primaryContainer] of our custom [MaterialTheme.colorScheme] if
 *  our [Boolean] parameter [selected] is `true`, or the [ColorScheme.surface] of our custom
 *  [MaterialTheme.colorScheme] if it is `false`.
 *  - `border`: is a [BorderStroke] whose `width` is `1.dp`, and whose [Color] `color` is the
 *  [ColorScheme.primary] of our custom [MaterialTheme.colorScheme] if our [Boolean] parameter
 *  [selected] is `true`, or the [ColorScheme.outline] of our custom [MaterialTheme.colorScheme]
 *  if it is `false`.
 *  - `modifier`: chains to our [Modifier] parameter [modifier] a [Modifier.clip] whose `shape`
 *  is the [Shapes.small] of our custom [MaterialTheme.shapes], and chains a [Modifier.selectable]
 *  to that whose `selected` argument is our [Boolean] parameter [selected], whose `onClick`
 *  argument is our [onOptionSelected] lambda parameter, and whose `role` argument is
 *  [Role.RadioButton].
 *
 * In the `content` composable lambda argument of the [Surface] we compose a [Row] whose `modifier`
 * argument is a [Modifier.fillMaxWidth], chained to a [Modifier.padding] that adds `16.dp` to `all`
 * sides. In the [RowScope] `content` composable lambda argument of the [Row] we compose:
 *
 * **First** an [Image] whose arguments are:
 *  - `painter`: is the [Painter] returned by [painterResource] for resource ID [imageResourceId].
 *  - `contentDescription`: is `null`.
 *  - `modifier`: is a [Modifier.size] with a size of `56.dp`, chained to a [Modifier.clip] whose
 *  `shape` is the [Shapes.extraSmall] of our custom [MaterialTheme.shapes], and that is chained
 *  to a [Modifier.padding] that adds `0.dp` to the `start` side, and `8.dp` to the `end` side.
 *
 * **Second** a [Spacer] whose `modifier` argument is a [Modifier.width] with a width of `8.dp`.
 *
 * **Third** a [Text] whose arguments are:
 *  - `text`: is our [String] parameter [text].
 *  - `modifier`: is a [RowScope.weight] with a weight of `1f`.
 *  - `style`: is the [Typography.bodyLarge] of our custom [MaterialTheme.typography].
 *
 * **Fourth** a [Box] whose `modifier` argument is a [Modifier.padding] that adds `8.dp` to `all`
 * sides. In the [BoxScope] `content` composable lambda argument of the [Box] we compose a
 * [RadioButton] whose arguments are:
 *  - `selected`: is our [Boolean] parameter [selected].
 *  - `onClick`: is `null`.
 *
 * @param text The text to display.
 * @param imageResourceId The drawable resource ID for the image to display.
 * @param selected Whether the radio button is selected.
 * @param onOptionSelected Callback that is invoked when the radio button is selected.
 * @param modifier Modifier to be applied to the composable.
 */
@Composable
fun RadioButtonWithImageRow(
    text: String,
    @DrawableRes imageResourceId: Int,
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
            .selectable(
                selected = selected,
                onClick = onOptionSelected,
                role = Role.RadioButton
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageResourceId),
                contentDescription = null,
                modifier = Modifier
                    .size(size = 56.dp)
                    .clip(shape = MaterialTheme.shapes.extraSmall)
                    .padding(start = 0.dp, end = 8.dp)
            )
            Spacer(modifier = Modifier.width(width = 8.dp))

            Text(
                text = text,
                modifier = Modifier.weight(weight = 1f),
                style = MaterialTheme.typography.bodyLarge
            )
            Box(modifier = Modifier.padding(all = 8.dp)) {
                RadioButton(selected = selected, onClick = null)
            }
        }
    }
}

/**
 * Preview of the [SingleChoiceQuestion] composable
 */
@Preview
@Composable
fun SingleChoiceQuestionPreview() {
    val possibleAnswers = listOf(
        Superhero(stringResourceId = R.string.spark, imageResourceId = R.drawable.spark),
        Superhero(stringResourceId = R.string.lenz, imageResourceId = R.drawable.lenz),
        Superhero(stringResourceId = R.string.bugchaos, imageResourceId = R.drawable.bug_of_chaos),
    )
    var selectedAnswer: Superhero? by remember { mutableStateOf(value = null) }

    SingleChoiceQuestion(
        titleResourceId = R.string.pick_superhero,
        directionsResourceId = R.string.select_one,
        possibleAnswers = possibleAnswers,
        selectedAnswer = selectedAnswer,
        onOptionSelected = { selectedAnswer = it },
    )
}

/**
 * A class that represents a superhero.
 *
 * @param stringResourceId The string resource ID for the superhero's name.
 * @param imageResourceId The drawable resource ID for the superhero's image.
 */
data class Superhero(@param:StringRes val stringResourceId: Int, @param:DrawableRes val imageResourceId: Int)
