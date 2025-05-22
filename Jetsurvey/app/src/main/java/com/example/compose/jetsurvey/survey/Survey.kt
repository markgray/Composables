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

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.compose.jetsurvey.R
import com.example.compose.jetsurvey.survey.question.DateQuestion
import com.example.compose.jetsurvey.survey.question.MultipleChoiceQuestion
import com.example.compose.jetsurvey.survey.question.PhotoQuestion
import com.example.compose.jetsurvey.survey.question.SingleChoiceQuestion
import com.example.compose.jetsurvey.survey.question.SliderQuestion
import com.example.compose.jetsurvey.survey.question.Superhero

/**
 * Composable for the free time question. Our root composable is a [MultipleChoiceQuestion] whose
 * arguments are:
 *  - `titleResourceId`: is the [String] with resource ID `R.string.in_my_free_time` ("In my free
 *  time I like to …").
 *  - `directionsResourceId`: is the [String] with resource ID `R.string.select_all` ("Select all
 *  that apply").
 *  - `possibleAnswers`: is a [List] of [Int]s whose elements are the string resource IDs of the
 *  possible answers: `R.string.read` ("Read"), `R.string.work_out` ("Work out"), `R.string.draw`,
 *  ("Draw")`, R.string.play_games` ("Play video games"), `R.string.dance` ("Dance"), and
 *  `R.string.watch_movies`. ("Watch movies").
 *  - `selectedAnswers`: is our [List] of [Int]s parameter [selectedAnswers].
 *  - `onOptionSelected`: is our [onOptionSelected] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *
 * @param selectedAnswers The list of selected answer resource IDs.
 * @param onOptionSelected Callback for when an option is selected.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun FreeTimeQuestion(
    selectedAnswers: List<Int>,
    onOptionSelected: (selected: Boolean, answer: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    MultipleChoiceQuestion(
        titleResourceId = R.string.in_my_free_time,
        directionsResourceId = R.string.select_all,
        possibleAnswers = listOf(
            R.string.read,
            R.string.work_out,
            R.string.draw,
            R.string.play_games,
            R.string.dance,
            R.string.watch_movies,
        ),
        selectedAnswers = selectedAnswers,
        onOptionSelected = onOptionSelected,
        modifier = modifier,
    )
}

/**
 * Composable for the superhero question. Our root composable is a [SingleChoiceQuestion] whose
 * arguments are:
 *  - `titleResourceId`: is the [String] with resource ID `R.string.pick_superhero` ("Pick a
 *  Compose comic character").
 *  - `directionsResourceId`: is the [String] with resource ID `R.string.select_one` ("Select one").
 *  - `possibleAnswers`: is a [List] of [Superhero]s whose elements are the possible answers.
 *  - `selectedAnswer`: is our [Superhero] parameter [selectedAnswer].
 *  - `onOptionSelected`: is our [onOptionSelected] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *
 * @param selectedAnswer The currently selected superhero.
 * @param onOptionSelected Callback for when a superhero is selected.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun SuperheroQuestion(
    selectedAnswer: Superhero?,
    onOptionSelected: (Superhero) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceQuestion(
        titleResourceId = R.string.pick_superhero,
        directionsResourceId = R.string.select_one,
        possibleAnswers = listOf(
            Superhero(
                stringResourceId = R.string.spark,
                imageResourceId = R.drawable.spark
            ),
            Superhero(
                stringResourceId = R.string.lenz,
                imageResourceId = R.drawable.lenz
            ),
            Superhero(
                stringResourceId = R.string.bugchaos,
                imageResourceId = R.drawable.bug_of_chaos
            ),
            Superhero(
                stringResourceId = R.string.frag,
                imageResourceId = R.drawable.frag
            ),
        ),
        selectedAnswer = selectedAnswer,
        onOptionSelected = onOptionSelected,
        modifier = modifier,
    )
}

/**
 * Composable for the takeaway question. Our root composable is a [DateQuestion] whose arguments are:
 *  - `titleResourceId`: is the [String] with resource ID `R.string.takeaway` ("When was the last time
 *  you ordered takeout...").
 *  - `directionsResourceId`: is the [String] with resource ID `R.string.select_date` ("Select date").
 *  - `dateInMillis`: is our [Long] parameter [dateInMillis].
 *  - `onClick`: is our [onClick] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *
 * @param dateInMillis The selected date in milliseconds.
 * @param onClick Callback for when the date is clicked.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun TakeawayQuestion(
    dateInMillis: Long?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DateQuestion(
        titleResourceId = R.string.takeaway,
        directionsResourceId = R.string.select_date,
        dateInMillis = dateInMillis,
        onClick = onClick,
        modifier = modifier,
    )
}

/**
 * Composable for the question about feelings about selfies. Our root composable is a [SliderQuestion]
 * whose arguments are:
 *  - `titleResourceId`: is the [String] with resource ID `R.string.selfies` ("How do you feel
 *  about selfies?").
 *  - `value`: is our [Float] parameter [value].
 *  - `onValueChange`: is our [onValueChange] lambda parameter.
 *  - `startTextResource`: is the [String] with resource ID `R.string.strongly_dislike` ("Strongly
 *  dislike") which is to be displayed at the start of the slider.
 *  - `neutralTextResource`: is the [String] with resource ID `R.string.neutral` ("Neutral") which
 *  is to be displayed at the middle of the slider.
 *  - `endTextResource`: is the [String] with resource ID `R.string.strongly_like` ("Strongly like")
 *  which is to be displayed at the end of the slider.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *
 * @param value The current value of the slider.
 * @param onValueChange Callback for when the slider value changes.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun FeelingAboutSelfiesQuestion(
    value: Float?,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    SliderQuestion(
        titleResourceId = R.string.selfies,
        value = value,
        onValueChange = onValueChange,
        startTextResource = R.string.strongly_dislike,
        neutralTextResource = R.string.neutral,
        endTextResource = R.string.strongly_like,
        modifier = modifier,
    )
}

/**
 * Displays a question prompting the user to take a selfie. This composable uses the [PhotoQuestion]
 * composable to handle the photo taking logic.
 *
 * our root composable is a [PhotoQuestion] whose arguments are:
 *  - `titleResourceId`: is the [String] with resource ID `R.string.selfie_skills` ("Show off your
 *  selfie skills").
 *  - `imageUri`: is our [Uri] parameter [imageUri].
 *  - `getNewImageUri`: is our [getNewImageUri] lambda parameter.
 *  - `onPhotoTaken`: is our [onPhotoTaken] lambda parameter.
 *  - `modifier`: is our [Modifier] parameter [modifier].
 *
 * @param imageUri The [Uri] of the image that has been taken, or `null` if no photo has been taken yet.
 * @param getNewImageUri A lambda function that returns a new [Uri] for the image to be saved to.
 * @param onPhotoTaken A lambda function that is called when a photo has been successfully taken,
 * with the [Uri] of the saved image as a parameter.
 * @param modifier A [Modifier] to be applied to the composable.
 */
@Composable
fun TakeSelfieQuestion(
    imageUri: Uri?,
    getNewImageUri: () -> Uri,
    onPhotoTaken: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    PhotoQuestion(
        titleResourceId = R.string.selfie_skills,
        imageUri = imageUri,
        getNewImageUri = getNewImageUri,
        onPhotoTaken = onPhotoTaken,
        modifier = modifier,
    )
}
