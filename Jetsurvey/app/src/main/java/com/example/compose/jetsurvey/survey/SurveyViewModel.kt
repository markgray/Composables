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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compose.jetsurvey.survey.question.Superhero

/**
 * Date format pattern used to display dates in the survey.
 *
 * For example, "Mon, Aug 31"
 */
const val simpleDateFormatPattern: String = "EEE, MMM d"

/**
 * ViewModel for the survey. Manages the survey questions, UI state and actions.
 *
 * @property photoUriManager Manages the URI for the selfie photo.
 */
class SurveyViewModel(
    private val photoUriManager: PhotoUriManager
) : ViewModel() {

    /**
     * The order in which the survey questions are presented.
     */
    private val questionOrder: List<SurveyQuestion> = listOf(
        SurveyQuestion.FREE_TIME,
        SurveyQuestion.SUPERHERO,
        SurveyQuestion.LAST_TAKEAWAY,
        SurveyQuestion.FEELING_ABOUT_SELFIES,
        SurveyQuestion.TAKE_SELFIE,
    )

    /**
     * Index of the current question in the [List] of [SurveyQuestion] property [questionOrder].
     */
    private var questionIndex = 0

    // ----- Responses exposed as State -----


    /**
     * A list of answer IDs for the free time question.
     *
     * For example, if the user selected "Reading" and "Exercising", this list would contain
     * the IDs for those two answers.
     */
    private val _freeTimeResponse: SnapshotStateList<Int> = mutableStateListOf<Int>()

    /**
     * Public read-only access to our [SnapshotStateList] of [Int] property  [_freeTimeResponse].
     */
    val freeTimeResponse: List<Int>
        get() = _freeTimeResponse

    /**
     * The superhero selected by the user.
     *
     * Null if the user has not yet selected a superhero.
     */
    private val _superheroResponse: MutableState<Superhero?> = mutableStateOf<Superhero?>(null)

    /**
     * Public read-only access to our [MutableState] of [Superhero] property [_superheroResponse].
     */
    val superheroResponse: Superhero?
        get() = _superheroResponse.value

    /**
     * The timestamp of the last takeaway, in milliseconds.
     *
     * Null if the user has not yet selected a takeaway date.
     */
    private val _takeawayResponse: MutableState<Long?> = mutableStateOf<Long?>(null)

    /**
     * Public read-only access to our [MutableState] of [Long] property [_takeawayResponse].
     */
    val takeawayResponse: Long?
        get() = _takeawayResponse.value

    /**
     * The user's response to the "How do you feel about selfies?" question.
     *
     * This is a float value between 0.0 and 1.0, where 0.0 means "Strongly dislike" and 1.0 means
     * "Strongly like".
     *
     * Null if the user has not yet answered the question.
     */
    private val _feelingAboutSelfiesResponse: MutableState<Float?> = mutableStateOf<Float?>(null)

    /**
     * Public read-only access to our [MutableState] of [Float] property [_feelingAboutSelfiesResponse].
     */
    val feelingAboutSelfiesResponse: Float?
        get() = _feelingAboutSelfiesResponse.value

    /**
     * The URI of the selfie photo.
     *
     * Null if the user has not yet taken a selfie.
     */
    private val _selfieUri: MutableState<Uri?> = mutableStateOf<Uri?>(null)

    /**
     * Public read-only access to our [MutableState] of [Uri] property [_selfieUri].
     */
    val selfieUri: Uri?
        get() = _selfieUri.value

    // ----- Survey status exposed as State -----

    /**
     * The current state of the survey screen, including the current question, question index,
     * total number of questions, and whether the "Previous" and "Done" buttons should be shown.
     */
    private val _surveyScreenData: MutableState<SurveyScreenData> =
        mutableStateOf(createSurveyScreenData())

    /**
     * Public read-only access to our [MutableState] of [SurveyScreenData] property [_surveyScreenData].
     */
    val surveyScreenData: SurveyScreenData?
        get() = _surveyScreenData.value

    /**
     * Whether the "Next" button should be enabled. This is `true` if the current question has been
     * answered, and `false` otherwise.
     */
    private val _isNextEnabled: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Public read-only access to our [MutableState] of [Boolean] property [_isNextEnabled].
     */
    val isNextEnabled: Boolean
        get() = _isNextEnabled.value

    /**
     * Returns `false` if the [questionIndex] is 0 (the ViewModel can not go back), otherwise it
     * calls [changeQuestion] with the previous question index to go back one question and returns
     * `true`.
     *
     * @return `false` if the [questionIndex] is 0, otherwise `true`.
     */
    fun onBackPressed(): Boolean {
        if (questionIndex == 0) {
            return false
        }
        changeQuestion(questionIndex - 1)
        return true
    }

    /**
     * Navigates to the previous question in the survey if it can. If already on the first (`0`)
     * question it throws [IllegalStateException]
     *
     * @throws IllegalStateException if called when on the first question.
     */
    fun onPreviousPressed() {
        if (questionIndex == 0) {
            throw IllegalStateException("onPreviousPressed when on question 0")
        }
        changeQuestion(questionIndex - 1)
    }

    /**
     * Called when the "Next" button is pressed.
     *
     * Moves to the next question in the survey.
     */
    fun onNextPressed() {
        changeQuestion(questionIndex + 1)
    }

    /**
     * Changes the current question to the question at the given index. We set our [questionIndex]
     * property to our [Int] parameter [newQuestionIndex] and call [getIsNextEnabled] to determine
     * whether the "Next" button should be enabled and set [_isNextEnabled] to the result. Then we
     * call [createSurveyScreenData] to create a new [SurveyScreenData] object and set
     * [_surveyScreenData] to the result.
     *
     * @param newQuestionIndex The index of the question to change to.
     */
    private fun changeQuestion(newQuestionIndex: Int) {
        questionIndex = newQuestionIndex
        _isNextEnabled.value = getIsNextEnabled()
        _surveyScreenData.value = createSurveyScreenData()
    }

    /**
     * Called when the "Done" button is pressed.
     *
     * This function should validate that the requirements of the survey are complete and then call
     * [onSurveyComplete]. Currently it just calls [onSurveyComplete].
     *
     * @param onSurveyComplete A lambda to call when the survey is complete.
     */
    fun onDonePressed(onSurveyComplete: () -> Unit) {
        // Here is where you could validate that the requirements of the survey are complete
        onSurveyComplete()
    }

    /**
     * Updates the free time response based on user selection.
     * If [selected] is true, the [answer] is added to the list of selected answers.
     * If [selected] is false, the [answer] is removed from the list of selected answers.
     * After updating the list, it checks if the "Next" button should be enabled.
     * TODO: Continue here.
     *
     * @param selected True if the answer was selected, false otherwise.
     * @param answer The ID of the answer that was selected or deselected.
     */
    fun onFreeTimeResponse(selected: Boolean, answer: Int) {
        if (selected) {
            _freeTimeResponse.add(answer)
        } else {
            _freeTimeResponse.remove(answer)
        }
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onSuperheroResponse(superhero: Superhero) {
        _superheroResponse.value = superhero
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onTakeawayResponse(timestamp: Long) {
        _takeawayResponse.value = timestamp
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onFeelingAboutSelfiesResponse(feeling: Float) {
        _feelingAboutSelfiesResponse.value = feeling
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun onSelfieResponse(uri: Uri) {
        _selfieUri.value = uri
        _isNextEnabled.value = getIsNextEnabled()
    }

    fun getNewSelfieUri(): Uri = photoUriManager.buildNewUri()

    private fun getIsNextEnabled(): Boolean {
        return when (questionOrder[questionIndex]) {
            SurveyQuestion.FREE_TIME -> _freeTimeResponse.isNotEmpty()
            SurveyQuestion.SUPERHERO -> _superheroResponse.value != null
            SurveyQuestion.LAST_TAKEAWAY -> _takeawayResponse.value != null
            SurveyQuestion.FEELING_ABOUT_SELFIES -> _feelingAboutSelfiesResponse.value != null
            SurveyQuestion.TAKE_SELFIE -> _selfieUri.value != null
        }
    }

    private fun createSurveyScreenData(): SurveyScreenData {
        return SurveyScreenData(
            questionIndex = questionIndex,
            questionCount = questionOrder.size,
            shouldShowPreviousButton = questionIndex > 0,
            shouldShowDoneButton = questionIndex == questionOrder.size - 1,
            surveyQuestion = questionOrder[questionIndex],
        )
    }
}

class SurveyViewModelFactory(
    private val photoUriManager: PhotoUriManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SurveyViewModel::class.java)) {
            return SurveyViewModel(photoUriManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class SurveyQuestion {
    FREE_TIME,
    SUPERHERO,
    LAST_TAKEAWAY,
    FEELING_ABOUT_SELFIES,
    TAKE_SELFIE,
}

data class SurveyScreenData(
    val questionIndex: Int,
    val questionCount: Int,
    val shouldShowPreviousButton: Boolean,
    val shouldShowDoneButton: Boolean,
    val surveyQuestion: SurveyQuestion,
)
