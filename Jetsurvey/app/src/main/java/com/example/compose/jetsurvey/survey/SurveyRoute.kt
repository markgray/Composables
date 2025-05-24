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

package com.example.compose.jetsurvey.survey

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.datepicker.MaterialDatePicker

/**
 * The duration of the slide animation in milliseconds when transitioning between questions.
 */
private const val CONTENT_ANIMATION_DURATION = 300

/**
 * Displays the [SurveyQuestionsScreen] and handles the survey state.
 *
 * We start by initializing our [SurveyViewModel] variable `viweModel` to the instance returned by
 * [viewModel] using as its `factory` argument our [SurveyViewModelFactory] to create a new instance
 * if it doesn't already exist. We initialize our [SurveyScreenData] variable `surveyScreenData` to
 * the [SurveyViewModel.surveyScreenData] property of our [SurveyViewModel] variable `viewModel` and
 * return if it is `null`.
 *
 * We compose a [BackHandler] that adds its `onBack` lambda argument to the `OnBackPressedDispatcher`
 * in which it checks if the [SurveyViewModel.onBackPressed] method of our [SurveyViewModel] variable
 * `viewModel` returns `false` which indicates that the ViewModel did not handle the back press to
 * move backward by one question so we should call our [onNavUp] lambda argument instead.
 *
 * Our root composable is a [SurveyQuestionsScreen] whose arguments are:
 *  - `surveyScreenData`: is our [SurveyScreenData] variable `surveyScreenData`.
 *  - `isNextEnabled`: is the [SurveyViewModel.isNextEnabled] property of our [SurveyViewModel]
 *  variable `viewModel`.
 *  - `onClosePressed`: is a lambda that calls our [onNavUp] lambda argument.
 *  - `onPreviousPressed`: is a lambda that calls the [SurveyViewModel.onPreviousPressed] method
 *  of our [SurveyViewModel] variable `viewModel`.
 *  - `onNextPressed`: is a lambda that calls the [SurveyViewModel.onNextPressed] method of our
 *  [SurveyViewModel] variable `viewModel`.
 *  - `onDonePressed`: is a lambda that calls the [SurveyViewModel.onDonePressed] method of our
 *  [SurveyViewModel] variable `viewModel` with its `onSurveyComplete` argument our [onSurveyComplete]
 *  lambda parameter.
 *
 * In the `content` composable lambda argument of the [SurveyQuestionsScreen] composable we accept
 * the [PaddingValues] passed the lambda in variable `paddingValues` and initialize our [Modifier]
 * variable to a [Modifier.padding] whose `paddingValues` argument is `paddingValues`. Then we
 * compose an [AnimatedContent] whose arguments are:
 *  - `targetState`: is our [SurveyScreenData] variable `surveyScreenData`.
 *  - `transitionSpec`: is an [AnimatedContentTransitionScope] of [SurveyScreenData] lambda in which
 *  we initialize our [TweenSpec] of [IntOffset] variable `animationSpec` to a [tween] whose
 *  `durationMillis` is [CONTENT_ANIMATION_DURATION] (`300`), then we initialize our
 *  [AnimatedContentTransitionScope.SlideDirection] variable `direction` to the value returned by
 *  [getTransitionDirection] for an `initialIndex` of the `initialState` of
 *  [SurveyScreenData.questionIndex] and a `targetIndex` of the `targetState` of
 *  [SurveyScreenData.questionIndex]. Finally it returns an
 *  [AnimatedContentTransitionScope.slideIntoContainer] whose `towards` argument is `direction`
 *  and whose `animationSpec` argument is `animationSpec` to which we then use the
 *  [EnterTransition.togetherWith] extension function to add a
 *  [AnimatedContentTransitionScope.slideOutOfContainer] whose `towards` argument is `direction`
 *  and whose `animationSpec` argument is `animationSpec`.
 *  - `label`: is a string literal with the value `"surveyScreenDataAnimation"`.
 *
 * In the [AnimatedContentScope] `content` lambda argument of the [AnimatedContent] we accept the
 * [SurveyScreenData] passed the lambda in variable `targetState` and then use a `when` statement
 * to branch on the vaule of the [SurveyScreenData.surveyQuestion] property of
 * [SurveyScreenData] variable `targetState`:
 *
 * [SurveyQuestion.FREE_TIME] -> we compose a [FreeTimeQuestion] whose arguments are:
 *  - `selectedAnswers`: is the [SurveyViewModel.freeTimeResponse] property of our [SurveyViewModel]
 *  variable `viewModel`.
 *  - `onOptionSelected`: is a function reference to the [SurveyViewModel.onFreeTimeResponse] method
 *  of our [SurveyViewModel] variable `viewModel`.
 *  - `modifier`: is our [Modifier] variable `modifier`.
 *
 * [SurveyQuestion.SUPERHERO] -> we compose a [SuperheroQuestion] whose arguments are:
 *  - `selectedAnswer`: is the [SurveyViewModel.superheroResponse] property of our [SurveyViewModel]
 *  variable `viewModel`.
 *  - `onOptionSelected`: is a function reference to the [SurveyViewModel.onSuperheroResponse] method
 *  of our [SurveyViewModel] variable `viewModel`.
 *  - `modifier`: is our [Modifier] variable `modifier`.
 *
 * [SurveyQuestion.LAST_TAKEAWAY] -> we initialize our [FragmentManager] variable `supportFragmentManager`
 * to the `supportFragmentManager` of our current [LocalContext] then we compose a [TakeawayQuestion]
 * whose arguments are:
 *  - `dateInMillis`: is the [SurveyViewModel.takeawayResponse] property of our [SurveyViewModel]
 *  variable `viewModel`.
 *  - `onClick`: is a lambda that calls the [showTakeawayDatePicker] method with its `date` argument
 *  the value of the [SurveyViewModel.takeawayResponse] property of our [SurveyViewModel] variable
 *  `viewModel`, its `supportFragmentManager` argument our [FragmentManager] variable
 *  `supportFragmentManager`, and its `onDateSelected` argument function reference to the
 *  [SurveyViewModel.onTakeawayResponse] method of our [SurveyViewModel] variable `viewModel`.
 *  - `modifier`: is our [Modifier] variable `modifier`.
 *
 * [SurveyQuestion.FEELING_ABOUT_SELFIES] -> we compose a [FeelingAboutSelfiesQuestion] composable
 * whose arguments are:
 *  - `value`: is the [SurveyViewModel.feelingAboutSelfiesResponse] property of our [SurveyViewModel]
 *  variable `viewModel`.
 *  - `onValueChange`: is a function reference to the [SurveyViewModel.onFeelingAboutSelfiesResponse]
 *  method of our [SurveyViewModel] variable `viewModel`.
 *  - `modifier`: is our [Modifier] variable `modifier`.
 *
 * [SurveyQuestion.TAKE_SELFIE] -> we compose a [TakeSelfieQuestion] composable whose arguments are:
 *  - `imageUri`: is the [SurveyViewModel.selfieUri] property of our [SurveyViewModel] variable
 *  `viewModel`.
 *  - `getNewImageUri`: is a function reference to the [SurveyViewModel.getNewSelfieUri] method of
 *  our [SurveyViewModel] variable `viewModel`.
 *  - `onPhotoTaken`: is a function reference to the [SurveyViewModel.onSelfieResponse] method of
 *  our [SurveyViewModel] variable `viewModel`.
 *  - `modifier`: is our [Modifier] variable `modifier`.
 *
 * @param onSurveyComplete Called when the survey is completed.
 * @param onNavUp Called when the user presses the back button or the close button and the survey
 * is not completed.
 */
@Composable
fun SurveyRoute(
    onSurveyComplete: () -> Unit,
    onNavUp: () -> Unit,
) {
    val viewModel: SurveyViewModel = viewModel(
        factory = SurveyViewModelFactory(photoUriManager = PhotoUriManager(LocalContext.current))
    )

    val surveyScreenData: SurveyScreenData = viewModel.surveyScreenData ?: return

    BackHandler {
        if (!viewModel.onBackPressed()) {
            onNavUp()
        }
    }

    SurveyQuestionsScreen(
        surveyScreenData = surveyScreenData,
        isNextEnabled = viewModel.isNextEnabled,
        onClosePressed = {
            onNavUp()
        },
        onPreviousPressed = { viewModel.onPreviousPressed() },
        onNextPressed = { viewModel.onNextPressed() },
        onDonePressed = { viewModel.onDonePressed(onSurveyComplete) }
    ) { paddingValues: PaddingValues ->

        val modifier = Modifier.padding(paddingValues = paddingValues)

        AnimatedContent(
            targetState = surveyScreenData,
            transitionSpec = {
                val animationSpec: TweenSpec<IntOffset> =
                    tween(durationMillis = CONTENT_ANIMATION_DURATION)

                val direction: AnimatedContentTransitionScope.SlideDirection =
                    getTransitionDirection(
                        initialIndex = initialState.questionIndex,
                        targetIndex = targetState.questionIndex,
                    )

                slideIntoContainer(
                    towards = direction,
                    animationSpec = animationSpec,
                ) togetherWith slideOutOfContainer(
                    towards = direction,
                    animationSpec = animationSpec
                )
            },
            label = "surveyScreenDataAnimation"
        ) { targetState: SurveyScreenData ->

            when (targetState.surveyQuestion) {
                SurveyQuestion.FREE_TIME -> {
                    FreeTimeQuestion(
                        selectedAnswers = viewModel.freeTimeResponse,
                        onOptionSelected = viewModel::onFreeTimeResponse,
                        modifier = modifier,
                    )
                }

                SurveyQuestion.SUPERHERO -> SuperheroQuestion(
                    selectedAnswer = viewModel.superheroResponse,
                    onOptionSelected = viewModel::onSuperheroResponse,
                    modifier = modifier,
                )

                SurveyQuestion.LAST_TAKEAWAY -> {
                    val supportFragmentManager: FragmentManager =
                        LocalContext.current.findActivity().supportFragmentManager
                    TakeawayQuestion(
                        dateInMillis = viewModel.takeawayResponse,
                        onClick = {
                            showTakeawayDatePicker(
                                date = viewModel.takeawayResponse,
                                supportFragmentManager = supportFragmentManager,
                                onDateSelected = viewModel::onTakeawayResponse
                            )
                        },
                        modifier = modifier,
                    )
                }

                SurveyQuestion.FEELING_ABOUT_SELFIES ->
                    FeelingAboutSelfiesQuestion(
                        value = viewModel.feelingAboutSelfiesResponse,
                        onValueChange = viewModel::onFeelingAboutSelfiesResponse,
                        modifier = modifier,
                    )

                SurveyQuestion.TAKE_SELFIE -> TakeSelfieQuestion(
                    imageUri = viewModel.selfieUri,
                    getNewImageUri = viewModel::getNewSelfieUri,
                    onPhotoTaken = viewModel::onSelfieResponse,
                    modifier = modifier,
                )
            }
        }
    }
}

/**
 * Determines the direction of the transition based on the initial and target question indices.
 *
 * If our [Int] parameter [targetIndex] target question index is greater than our
 * [Int] parameter [initialIndex] initial question index, then the transition
 * is an [AnimatedContentTransitionScope.SlideDirection.Left], if however we are
 * going back to the previous question in the set, then the transition is an
 * [AnimatedContentTransitionScope.SlideDirection.Right].
 *
 * @param initialIndex The index of the question being transitioned from.
 * @param targetIndex The index of the question being transitioned to.
 * @return The direction of the transition.
 */
private fun getTransitionDirection(
    initialIndex: Int,
    targetIndex: Int
): AnimatedContentTransitionScope.SlideDirection {
    return if (targetIndex > initialIndex) {
        // Going forwards in the survey: Set the initial offset to start
        // at the size of the content so it slides in from right to left, and
        // slides out from the left of the screen to -fullWidth
        AnimatedContentTransitionScope.SlideDirection.Left
    } else {
        // Going back to the previous question in the set, we do the same
        // transition as above, but with different offsets - the inverse of
        // above, negative fullWidth to enter, and fullWidth to exit.
        AnimatedContentTransitionScope.SlideDirection.Right
    }
}

/**
 * Displays a [MaterialDatePicker] to allow the user to select the date of their last takeaway.
 * We initialize our [MaterialDatePicker] of [Long] variable `val picker` by building a
 * [MaterialDatePicker.Builder.datePicker] whose selection is set to our [date] parameter.
 * We then show `picker` using our [supportFragmentManager] parameter as the [FragmentManager]
 * and the string value of `picker` as the `tag`. We add an `OnPositiveButtonClickListener` to
 * `picker` whose lambda will retrieve the [MaterialDatePicker.getSelection] of `picker` (the
 * selected date in milliseconds since the Epoch) and if it is not `null` call our [onDateSelected]
 * lambda parameter with it.
 *
 * @param date the initial date to select in the picker, or `null` for no initial selection.
 * This is expressed as the number of milliseconds from the epoch.
 * @param supportFragmentManager the [FragmentManager] to use to display the picker.
 * @param onDateSelected a lambda that will be called with the selected date in milliseconds since
 * the Epoch when the user clicks the positive button.
 */
private fun showTakeawayDatePicker(
    date: Long?,
    supportFragmentManager: FragmentManager,
    onDateSelected: (date: Long) -> Unit,
) {
    val picker: MaterialDatePicker<Long?> = MaterialDatePicker.Builder.datePicker()
        .setSelection(date)
        .build()
    picker.show(supportFragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener {
        picker.selection?.let { newDate: Long ->
            onDateSelected(newDate)
        }
    }
}

/**
 * Recursively searches for the [AppCompatActivity] associated with this [Context]. This method
 * uses tail recursion to traverse the context hierarchy until it finds an [AppCompatActivity].
 * It starts with the current context and checks if it's an instance of [AppCompatActivity].
 * If it is, that instance is returned. If the current context is a [ContextWrapper], the method
 * recursively calls itself with the base context of the wrapper. If the context is neither an
 * [AppCompatActivity] nor a [ContextWrapper], it means an activity could not be found, and an
 * [IllegalArgumentException] is thrown.
 *
 * @return The [AppCompatActivity] associated with this [Context].
 * @throws IllegalArgumentException if an [AppCompatActivity] cannot be found.
 */
private tailrec fun Context.findActivity(): AppCompatActivity =
    when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> this.baseContext.findActivity()
        else -> throw IllegalArgumentException("Could not find activity!")
    }
