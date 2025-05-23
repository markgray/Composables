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

package com.example.compose.jetsurvey.survey

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.theme.stronglyDeemphasizedAlpha
import com.example.compose.jetsurvey.util.supportWideScreen

/**
 * Displays a survey question screen with a top app bar, content area, and bottom navigation bar.
 *
 * Our root composable is a [Surface] whose `modifier` argument is a [Modifier.supportWideScreen].
 * In its `content` Composable lambda argument we compose a [Scaffold] whose arguments are:
 *  - `topBar`: is a [SurveyTopAppBar] whose `questionIndex` argument is the
 *  [SurveyScreenData.questionIndex]` of our [SurveyScreenData] variable [surveyScreenData], whose
 *  `totalQuestionsCount` argument is the [SurveyScreenData.questionCount] of our [SurveyScreenData]
 *  variable [surveyScreenData], and whose `onClosePressed` argument our lambda parameter
 *  [onClosePressed].
 *  - `content`: is our lambda parameter [content].
 *  - `bottomBar`: is a [SurveyBottomBar] whose `shouldShowPreviousButton` argument is the
 *  [SurveyScreenData.shouldShowPreviousButton] property of our [SurveyScreenData] variable
 *  [surveyScreenData], whose `shouldShowDoneButton` argument is the
 *  [SurveyScreenData.shouldShowDoneButton] property of our [SurveyScreenData] variable
 *  [surveyScreenData], whose `isNextButtonEnabled` argument is our [Boolean] parameter
 *  [isNextEnabled], whose `onPreviousPressed` argument our lambda parameter [onPreviousPressed],
 *  whose `onNextPressed` argument our lambda parameter [onNextPressed], and whose `onDonePressed`
 *  argument our lambda parameter [onDonePressed].
 *
 * @param surveyScreenData Data related to the current survey screen, including question index and count.
 * @param isNextEnabled Whether the "Next" button should be enabled.
 * @param onClosePressed Callback invoked when the close button in the top app bar is pressed.
 * @param onPreviousPressed Callback invoked when the "Previous" button is pressed.
 * @param onNextPressed Callback invoked when the "Next" button is pressed.
 * @param onDonePressed Callback invoked when the "Done" button is pressed.
 * @param content A composable function that defines the content of the survey question. It receives
 * the [PaddingValues] that [Scaffold] passes to its `content` composable lambda argument.
 */
@Composable
fun SurveyQuestionsScreen(
    surveyScreenData: SurveyScreenData,
    isNextEnabled: Boolean,
    onClosePressed: () -> Unit,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {

    Surface(modifier = Modifier.supportWideScreen()) {
        Scaffold(
            topBar = {
                SurveyTopAppBar(
                    questionIndex = surveyScreenData.questionIndex,
                    totalQuestionsCount = surveyScreenData.questionCount,
                    onClosePressed = onClosePressed,
                )
            },
            content = content,
            bottomBar = {
                SurveyBottomBar(
                    shouldShowPreviousButton = surveyScreenData.shouldShowPreviousButton,
                    shouldShowDoneButton = surveyScreenData.shouldShowDoneButton,
                    isNextButtonEnabled = isNextEnabled,
                    onPreviousPressed = onPreviousPressed,
                    onNextPressed = onNextPressed,
                    onDonePressed = onDonePressed
                )
            }
        )
    }
}

/**
 * Shows the survey result screen.
 *
 * Our root composable is a [Surface] whose `modifier` argument is a [Modifier.supportWideScreen].
 * In its `content` Composable lambda argument we compose a [Scaffold] whose arguments are:
 *  - `content`: is a lambda that accepts the [PaddingValues] passed the lambda in variable
 *  `innerPadding` then initializes its [Modifier] variable `modifier` to a [Modifier.padding]
 *  whose `paddingValues` argument is `innerPadding`. Then it composes a [SurveyResult]
 *  whose `title` argument is the string resource with id `R.string.survey_result_title`
 *  ("Compose"), whose `subtitle` argument is the string resource with id
 *  `R.string.survey_result_subtitle` ("Congratulations, you are Compose"), whose `description`
 *  argument is the string resource with id `R.string.survey_result_description` ("You are a curious
 *  developer, always willing to..."), and whose `modifier` argument is our [Modifier] parameter
 *  `modifier`.
 *  - `bottomBar`: is an [OutlinedButton] whose `onClick` argument our lambda parameter [onDonePressed],
 *  whose `modifier` argument is a [Modifier.fillMaxWidth] chained to a [Modifier.padding] that adds
 *  `20.dp` to each `horizontal` side and `24.dp` to each `vertical` side. In its [RowScope] `content`
 *  composable lambda argument we compose a [Text] whose `text` argument is the string resource with
 *  Id `R.string.done` ("Done").
 *
 * @param onDonePressed Callback to be called when the "Done" button is pressed.
 */
@Composable
fun SurveyResultScreen(
    onDonePressed: () -> Unit,
) {

    Surface(modifier = Modifier.supportWideScreen()) {
        Scaffold(
            content = { innerPadding: PaddingValues ->
                val modifier = Modifier.padding(paddingValues = innerPadding)
                SurveyResult(
                    title = stringResource(id = R.string.survey_result_title),
                    subtitle = stringResource(id = R.string.survey_result_subtitle),
                    description = stringResource(id = R.string.survey_result_description),
                    modifier = modifier
                )
            },
            bottomBar = {
                OutlinedButton(
                    onClick = onDonePressed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            }
        )
    }
}

/**
 * Displays the survey result.
 *
 * This composable consists of a [LazyColumn] which holds three [Text] composables that display
 * its [String] parameters [title], [subtitle], and [description].
 *
 * Our root composable is a [LazyColumn] whose `modifier` argument is a [Modifier.fillMaxSize].
 * In its [LazyListScope] `content` Composable lambda argument we compose a [LazyListScope.item],and
 * in its [LazyItemScope] Composable lambda argument we compose:
 *
 * **First** a [Spacer] whose `modifier` argument is a [Modifier.height] of `44.dp`.
 *
 * **Second** a [Text] whose arguments are:
 *  - `text`: is our [String] parameter [title].
 *  - `style`: is the [Typography.displaySmall] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] whose `horizontal` argument is `20.dp`.
 *
 * **Third** a [Text] whose arguments are:
 *  - `text`: is our [String] parameter [subtitle].
 *  - `style`: is the [Typography.titleMedium] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] whose `all` argument is `20.dp`.
 *
 * **Fourth** a [Text] whose arguments are:
 *  - `text`: is our [String] parameter [description].
 *  - `style`: is the [Typography.bodyLarge] of our custom [MaterialTheme.typography].
 *  - `modifier`: is a [Modifier.padding] whose `horizontal` argument is `20.dp`.
 *
 * @param title The title of the survey result.
 * @param subtitle The subtitle of the survey result.
 * @param description The description of the survey result.
 * @param modifier A [Modifier] that will be applied to the [LazyColumn].
 */
@Composable
private fun SurveyResult(
    title: String,
    subtitle: String,
    description: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(height = 44.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(all = 20.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

/**
 * Composable that displays the current question number and total number of questions.
 *
 * It is used as the `title` of the [CenterAlignedTopAppBar] used by [SurveyTopAppBar].
 * It consists of a [Row] holding two [Text] widgets. The first [Text] displays the value of the
 * [Int] parameter [questionIndex] plus 1 (to convert from 0-based to 1-based) as its `text`,
 * with its `style` argument [Typography.labelMedium] of our custom [MaterialTheme.typography], and
 * its `color` a copy of the [ColorScheme.onSurface] color of our custom [MaterialTheme.colorScheme]
 * with its `alpha` forced to [stronglyDeemphasizedAlpha] (0.6f). The second [Text] displays the
 * string constructed using the format string with resource ID `R.string.question_count` (" of %1$d")
 * and [totalQuestionsCount] as the argument, with its `style` argument [Typography.labelMedium] of
 * our custom [MaterialTheme.typography], and its `color` a copy of the [ColorScheme.onSurface] color
 * of our custom [MaterialTheme.colorScheme] with its `alpha` forced to 0.38f.
 *
 * @param questionIndex The index of the current question (0-based).
 * @param totalQuestionsCount The total number of questions in the survey.
 * @param modifier A [Modifier] that will be applied to the [Row].
 */
@Composable
private fun TopAppBarTitle(
    questionIndex: Int,
    totalQuestionsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = (questionIndex + 1).toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = stronglyDeemphasizedAlpha)
        )
        Text(
            text = stringResource(R.string.question_count, totalQuestionsCount),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

/**
 * Displays the top app bar for a survey screen.
 *
 * It consists of a [Column] holding a [CenterAlignedTopAppBar] and a [LinearProgressIndicator].
 *
 * Our root composable is a [Column] whose `modifier` argument is a [Modifier.fillMaxWidth]. In
 * its [ColumnScope] `content` Composable lambda argument we compose a [CenterAlignedTopAppBar]
 * whose arguments are:
 *  - `title`: is a lambda that composes a [TopAppBarTitle] whose `questionIndex` argument is our
 *  [Int] parameter [questionIndex], and whose `totalQuestionsCount` argument is our [Int] parameter
 *  [totalQuestionsCount].
 *  - `actions`: is a lambda that composes an [IconButton] whose `onClick` argument is our lambda
 *  parameter [onClosePressed], and whose `modifier` argument is a [Modifier.padding] that adds
 *  `4.dp` to `all` sides. The `content` Composable lambda argument of the [IconButton] is an [Icon]
 *  whose `imageVector` argument is the [ImageVector] drawn by [Icons.Filled.Close] (an "X"), whose
 *  `contentDescription` is the string resource with ID `R.string.close` ("Close"), and whose `tint`
 *  is a copy of the [ColorScheme.onSurface] color of our custom [MaterialTheme.colorScheme] with
 *  its `alpha` forced to [stronglyDeemphasizedAlpha] (0.6f).
 *
 * We then initialize our [State] wrapped animated [Float] variable `animatedProgress` to the value
 * returned by [animateFloatAsState] whose `targetValue` argument is the value of our [Int] parameter
 * [questionIndex] plus 1 divided by our [Int] parameter [totalQuestionsCount] cast to a [Float], and
 * whose `animationSpec` argument is the default [ProgressIndicatorDefaults.ProgressAnimationSpec].
 *
 * WE then compose a [LinearProgressIndicator] which has its `progress` argument set a lambda that
 * returns the value of our [State] wrapped animated [Float] variable `animatedProgress`. The
 * `modifier` argument of the [LinearProgressIndicator] is a [Modifier.fillMaxWidth] that causes it
 * to fill the entire incoming width constraint, chained to a [Modifier.padding] that adds `20.dp`
 * to the `horizontal` sides. Its `trackColor` is a copy of the [ColorScheme.onSurface] color of our
 * custom [MaterialTheme.colorScheme] with its `alpha` forced to 0.12f
 *
 * @param questionIndex The index of the current question (0-based).
 */
@OptIn(ExperimentalMaterial3Api::class) // CenterAlignedTopAppBar is experimental in m3
@Composable
fun SurveyTopAppBar(
    questionIndex: Int,
    totalQuestionsCount: Int,
    onClosePressed: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        CenterAlignedTopAppBar(
            title = {
                TopAppBarTitle(
                    questionIndex = questionIndex,
                    totalQuestionsCount = totalQuestionsCount,
                )
            },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(all = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(id = R.string.close),
                        tint = MaterialTheme.colorScheme.onSurface
                            .copy(alpha = stronglyDeemphasizedAlpha)
                    )
                }
            }
        )

        val animatedProgress: Float by animateFloatAsState(
            targetValue = (questionIndex + 1) / totalQuestionsCount.toFloat(),
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}

/**
 * Bottom bar for a survey screen, optionly containing "Previous", "Next", and/or "Done" buttons.
 *
 * Our root composable is a [Surface] whose `shadowElevation` argument is `7.dp`. In its `content`
 * Composable lambda argument we compose a [Row] whose `modifier` argument is a [Modifier.fillMaxWidth]
 * chained to a [Modifier.windowInsetsPadding] whose `insets` argument is a
 * [WindowInsets.Companion.systemBars] that includes only the [Horizontal] and [Bottom] system
 * [WindowInsetsSides] (this is done to implement our own edge-to-edge support since this is not
 * a Material component), and at the end of the chain is a [Modifier.padding] that add `16.dp` to
 * the `horizontal` sides and `20.dp` to the `vertical` sides.
 *
 * In the [RowScope] `content` Composable lambda argument of the [Row] we have three if statements
 * that chose which buttons to show.
 *
 * If our [Boolean] parameter [shouldShowPreviousButton] is `true`, we compose a [OutlinedButton]
 * whose arguments are:
 *  - `modifier`: is a [RowScope.weight] whose `weight` argument is `1f`, chained to a
 *  [Modifier.height] whose `height` argument is `48.dp`.
 *  - `onClick`: is our lambda parameter [onPreviousPressed].
 *  - `content`: is a [RowScope] composable lambda that composes a [Text] whose `text` argument is
 *  the string whose resource ID is `R.string.previous` ("Previous").
 *  - Below the [OutlinedButton] we compose a [Spacer] whose `modifier` argument is a [Modifier.width]
 *  whose `width` argument is `16.dp`.
 *
 * If our [Boolean] parameter [shouldShowDoneButton] is `true`, we compose a [Button] whose arguments
 * are:
 *  - `modifier`: is a [RowScope.weight] whose `weight` argument is `1f`, chained to a [Modifier.height]
 *  whose `height` argument is `48.dp`.
 *  - `onClick`: is our lambda parameter [onDonePressed].
 *  - `enabled`: is our [Boolean] parameter [isNextButtonEnabled].
 *  - `content`: is a [RowScope] composable lambda that composes a [Text] whose `text` argument is
 *  the string whose resource ID is `R.string.done` ("Done").
 *
 * Else if our [Boolean] parameter [shouldShowDoneButton] is `false`, we compose a [Button] whose
 * arguments are:
 *  - `modifier`: is a [RowScope.weight] whose `weight` argument is `1f`, chained to a
 *  [Modifier.height] whose `height` argument is `48.dp`.
 *  - `onClick`: is our lambda parameter [onNextPressed].
 *  - `enabled`: is our [Boolean] parameter [isNextButtonEnabled].
 *  - `content`: is a [RowScope] composable lambda that composes a [Text] whose `text` argument is
 *  the string whose resource ID is `R.string.next` ("Next").
 *
 * @param shouldShowPreviousButton Whether the "Previous" button should be shown.
 * @param shouldShowDoneButton Whether the "Done" button should be shown.
 * @param isNextButtonEnabled Whether the "Next" button should be enabled.
 * @param onPreviousPressed Callbackto be invoked when the "Previous" button is pressed.
 * @param onNextPressed Callback to be invoked when the "Next" button is pressed.
 * @param onDonePressed Callback to be invoked when the "Done" button is pressed.
 */
@Composable
fun SurveyBottomBar(
    shouldShowPreviousButton: Boolean,
    shouldShowDoneButton: Boolean,
    isNextButtonEnabled: Boolean,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit
) {
    Surface(shadowElevation = 7.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Since we're not using a Material component but we implement our own bottom bar,
                // we will also need to implement our own edge-to-edge support. Similar to the
                // NavigationBar, we add the horizontal and bottom padding if it hasn't been consumed yet.
                .windowInsetsPadding(insets = WindowInsets.systemBars.only(Horizontal + Bottom))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (shouldShowPreviousButton) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .height(height = 48.dp),
                    onClick = onPreviousPressed
                ) {
                    Text(text = stringResource(id = R.string.previous))
                }
                Spacer(modifier = Modifier.width(width = 16.dp))
            }
            if (shouldShowDoneButton) {
                Button(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .height(height = 48.dp),
                    onClick = onDonePressed,
                    enabled = isNextButtonEnabled,
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            } else {
                Button(
                    modifier = Modifier
                        .weight(weight = 1f)
                        .height(height = 48.dp),
                    onClick = onNextPressed,
                    enabled = isNextButtonEnabled,
                ) {
                    Text(text = stringResource(id = R.string.next))
                }
            }
        }
    }
}
