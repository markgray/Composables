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

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.QuestionWrapper
import com.example.compose.jetsurvey.theme.JetsurveyTheme

/**
 * Slider question, a question that consists of a slider allowing an answer in a given range.
 *
 * We start by initializing and remembering our [MutableFloatState] variable `sliderPosition` with
 * our [Float] parameter [value] as its initial value if it is not `null` or the middle of our
 * [ClosedFloatingPointRange] parameter [valueRange] if it is `null`.
 *
 * Our root composable is a [QuestionWrapper] whose `titleResourceId` argument is our [Int] parameter
 * [titleResourceId], and whose `modifier` argument is our [Modifier] parameter [modifier]. Inside
 * its `content` composable lambda argument we compose two [Row]'s (these are inside the scrollable
 * [Column] of the [QuestionWrapper]).
 *
 * **First Row** In the [RowScope] `content` composable lambda argument we compose a [Slider] whose
 * arguments are:
 *  - `value`: is our [MutableState] wrapped [Float] variable `sliderPosition`.
 *  - `onValueChange`: is a lambda that accepts the [Float] passed the lambda in variable `position`
 *  then sets `sliderPostition` to `position` and calls our [onValueChange] lambda parameter with
 *  `position`.
 *  - `valueRange`: is our [ClosedFloatingPointRange] parameter [valueRange].
 *  - `steps`: is our [Int] parameter [steps].
 *  - `modifier`: is a [Modifier.padding] that adds `16.dp` to the `horizontal` sides, with a
 *  [Modifier.fillMaxWidth] chained to that which causes it to fill the entire width of the [Row].
 *
 * **Second Row** In the [RowScope] `content` composable lambda argument we compose threes [Text]s:
 *
 * **First** a [Text] whose arguments are:
 *  - `text`: is the [String] whose resource ID is our [Int] parameter [startTextResource].
 *  - `style`: is the [Typography.bodySmall] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.Start].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [RowScope.weight] with a weight of `1.8f`
 *
 * **Second** a [Text] whose arguments are:
 *  - `text`: is the [String] whose resource ID is our [Int] parameter [neutralTextResource].
 *  - `style`: is the [Typography.bodySmall] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.Center].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [RowScope.weight] with a weight of `1.8f`
 *
 * **Third** a [Text] whose arguments are:
 *  - `text`: is the [String] whose resource ID is our [Int] parameter [endTextResource].
 *  - `style`: is the [Typography.bodySmall] of our custom [MaterialTheme.typography].
 *  - `textAlign`: is [TextAlign.End].
 *  - `modifier`: is a [Modifier.fillMaxWidth] chained to a [RowScope.weight] with a weight of `1.8f`
 *
 * @param titleResourceId String resource to be used for the question's title
 * @param value Current value of the answer. If `null`, the slider will be positioned in the middle.
 * @param onValueChange Callback to be invoked when the user changes the slider value.
 * @param modifier Modifier to be applied to the slider.
 * @param valueRange Range of values that the slider can take.
 * @param steps Number of discrete values that the slider can take.
 * @param startTextResource String resource to be used for the text displayed at the start of the
 * slider.
 * @param neutralTextResource String resource to be used for the text displayed in the middle of the
 * slider.
 * @param endTextResource String resource to be used for the text displayed at the end of the slider.
 */
@Composable
fun SliderQuestion(
    @StringRes titleResourceId: Int,
    value: Float?,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 3,
    @StringRes startTextResource: Int,
    @StringRes neutralTextResource: Int,
    @StringRes endTextResource: Int
) {
    var sliderPosition: Float by remember {
        mutableFloatStateOf(value ?: ((valueRange.endInclusive - valueRange.start) / 2))
    }
    QuestionWrapper(
        titleResourceId = titleResourceId,
        modifier = modifier,
    ) {

        Row {
            Slider(
                value = sliderPosition,
                onValueChange = { position: Float ->
                    sliderPosition = position
                    onValueChange(position)
                },
                valueRange = valueRange,
                steps = steps,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            )
        }
        Row {
            Text(
                text = stringResource(id = startTextResource),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1.8f)
            )
            Text(
                text = stringResource(id = neutralTextResource),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1.8f)
            )
            Text(
                text = stringResource(id = endTextResource),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1.8f)
            )
        }
    }
}

/**
 * Two Previews of the [SliderQuestion] composable:
 *  - One with the light theme
 *  - One with the dark theme
 */
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SliderQuestionPreview() {
    JetsurveyTheme {
        Surface {
            SliderQuestion(
                titleResourceId = R.string.selfies,
                value = 0.4f,
                onValueChange = {},
                startTextResource = R.string.strongly_dislike,
                endTextResource = R.string.strongly_like,
                neutralTextResource = R.string.neutral
            )
        }
    }
}
