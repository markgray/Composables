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
package com.compose.performance

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.compose.performance.accelerate.AccelerateHeavyScreen
import com.compose.performance.phases.PhasesAnimatedShape
import com.compose.performance.phases.PhasesComposeLogo
import com.compose.performance.stability.StabilityScreen
import com.compose.performance.ui.theme.PerformanceWorkshopTheme

/**
 * This is the Activity for the Performance with Jetpack Compose codelab.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. First we call [enableEdgeToEdge] to enable edge to
     * edge display, then we call our super's implementation of `onCreate`. We call [setContent]
     * to have it Compose its `content` composable lambda argument into our activity. In that
     * lambda we initialize our [PerformanceCodeLabViewModel] variable `val viewModel` to an
     * existing instance or a new instance if there is none whose `startFromStep` argument is the
     * [String] stored under the key [EXTRA_START_TASK] ("EXTRA_START_TASK") that we have retrieved
     * from the [Intent] that started the app to initialize our [String] variable `val startFromStep`.
     * Then wrapped in our [PerformanceWorkshopTheme] custom [MaterialTheme] we compose a [Surface]
     * whose `modifier` argument is a [Modifier.fillMaxSize] to have it take up its entire incoming
     * size constraints, with a [Modifier.semantics] chained to that sets the
     * [SemanticsPropertyReceiver.testTagsAsResourceId] property to `true` to map testTags to
     * resource-id to make them available to tests. The background `color` argument of the [Surface]
     * is the [ColorScheme.background] of our custom [MaterialTheme.colorScheme]. In the `content`
     * composable lambda argument we compose our [PerformanceCodeLabScreen] composable with its
     * `selectedPage` argument the [MutableState.value] of the [MutableState] wrapped [TaskScreen]
     * property [PerformanceCodeLabViewModel.selectedPage] of our [PerformanceCodeLabViewModel]
     * variable `viewModel`, and with its `onPageSelected` argument a lambda which accepts the
     * [TaskScreen] passed it in the variable `taskScreen`, then sets the [MutableState.value] of
     * the [MutableState] wrapped [TaskScreen] property [PerformanceCodeLabViewModel.selectedPage]
     * of our [PerformanceCodeLabViewModel] variable `viewModel` to the [TaskScreen] in the
     * `taskScreen` variable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use this.
     */
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: PerformanceCodeLabViewModel = viewModel {
                // Allows us to start the workshop from a various screen
                // so that we can have a run configuration for each task.
                val startFromStep: String? = intent.getStringExtra(EXTRA_START_TASK)
                PerformanceCodeLabViewModel(startFromStep = startFromStep)
            }

            PerformanceWorkshopTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .semantics { testTagsAsResourceId = true },
                    color = MaterialTheme.colorScheme.background
                ) {
                    PerformanceCodeLabScreen(
                        selectedPage = viewModel.selectedPage.value,
                        onPageSelected = { taskScreen: TaskScreen ->
                            viewModel.selectedPage.value = taskScreen
                        }
                    )
                }
            }
        }
    }
}

/**
 * This is the [ViewModel] that holds the [TaskScreen] that controls what [TaskScreen.composable]
 * [PerformanceCodeLabScreen] displays.
 *
 * @param startFromStep the [TaskScreen.id] of the start [TaskScreen], or if `null` it will default
 * to [TaskScreen.AccelerateHeavyScreen].
 */
private class PerformanceCodeLabViewModel(startFromStep: String?) : ViewModel() {
    /**
     * The [TaskScreen] whose [TaskScreen.composable] should be displayed by [PerformanceCodeLabScreen]
     */
    val selectedPage: MutableState<TaskScreen> = mutableStateOf(TaskScreen.from(startFromStep))
}

/**
 * This is the main screen of our app. We start by calling [BackHandler] with its `enabled` argument
 * `true` if [TaskScreen] parameter [selectedPage] if [TaskScreen.isFirst] returns `false`
 * (returns `false` if the [TaskScreen.ordinal] of [selectedPage] is not 0, so the [BackHandler] is
 * enabled for all [TaskScreen] except [TaskScreen.AccelerateHeavyScreen]). In the `onBack` lambda
 * argument of [BackHandler] we call our [onPageSelected] lambda with the [TaskScreen] that the
 * [TaskScreen.previous] method of [selectedPage] returns (this will be the [TaskScreen] whose
 * [TaskScreen.ordinal] is 1 less than the [TaskScreen.ordinal] of [selectedPage]). Our root
 * Composable is a [Column] whose `modifier` argument is a [Modifier.fillMaxSize] to have it take
 * up its entire incoming size constraints, with a [Modifier.systemBarsPadding] chained to that to
 * add padding to accommodate the system bars insets, and to that is chained a [Modifier.testTag]
 * to add the `tag` of the [TaskScreen.id] of [selectedPage] to allow the [Column] to be found in
 * tests. The `horizontalAlignment` argument of the [Column] is [Alignment.CenterHorizontally] to
 * center its children horizontally.
 *
 * In the `content` lambda argument of the [Column] we have:
 *  - a [Row] whose `verticalAlignment argument is [Alignment.CenterVertically] to center its children
 *  vertically, whose `modifier` argument is a [Modifier.fillMaxWidth] to have it occupy its entire
 *  incoming width constraint, and whose `horizontalArrangement` argument is [Arrangement.Center] to
 *  center the horizontal arrangement its children. In the [RowScope] `content` lambda argument of
 *  [Row] we first initialize our [String] variable `val previousTaskLabel` to the [String] with
 *  resource ID `R.string.previous_task` ("Previous task"), and our [String] variable `val nextTaskLabel`
 *  to the [String] with resource ID `R.string.next_task` ("Next task"). We then compose an [IconButton]
 *  whose `onClick` argument is a lambda that calls our [onPageSelected] lambda parameter with the
 *  [TaskScreen] returned by the [TaskScreen.previous] method of our [TaskScreen] parameter
 *  [selectedPage], and whose `modifier` argument is a [Modifier.semantics] with our [String] variable
 *  `previousTaskLabel` ("Previous task"). The `content` argument of the [IconButton] is an [Icon]
 *  whose `painter` argument is the remembered [VectorPainter] created from the `image` argument
 *  [Icons.AutoMirrored.Filled.ArrowBack]. Next in the [Row] is a [Text] displaying the `text` formed
 *  by concatenating the [String] "Task" followed by the [String] value of the [TaskScreen.ordinal]
 *  plus 1 of our [TaskScreen] parameter [selectedPage], followed by the [String] "/" followed by the
 *  [String] value of the total number of [TaskScreen] enums. (ie. "Task 1/4", "Task 2/4" ... etc).
 *  At the end of the [Row] is another [IconButton] whose `onClick` argument is a lambda that calls
 *  our [onPageSelected] lambda parameter with the [TaskScreen] returned by the [TaskScreen.next]
 *  method of our [TaskScreen] parameter [selectedPage], and whose `modifier` argument is a
 *  [Modifier.semantics] with our [String] variable `nextTaskLabel` ("Next task"). The `content`
 *  argument of the [IconButton] is an [Icon] whose `painter` argument is the remembered
 *  [VectorPainter] created from the `image` argument [Icons.AutoMirrored.Filled.ArrowForward]. *
 *  - Next in the [Column] is a [Text] displaying the `text` of the [TaskScreen.label] of our
 *  [TaskScreen] parameter [selectedPage].
 *  - There follows a [HorizontalDivider]
 *  - and at the bottom of the [Column] we compose the [TaskScreen.composable] composable lambda of
 *  our [TaskScreen] parameter [selectedPage].
 *
 * @param selectedPage the [TaskScreen] whose [TaskScreen.composable] we should display.
 * @param onPageSelected a lambda we should call with the [TaskScreen] that the user has chosen to
 * replace [selectedPage].
 */
@Composable
private fun PerformanceCodeLabScreen(
    selectedPage: TaskScreen,
    onPageSelected: (selected: TaskScreen) -> Unit
) {
    BackHandler(enabled = !selectedPage.isFirst) {
        onPageSelected(selectedPage.previous())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .testTag(tag = selectedPage.id),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val previousTaskLabel: String = stringResource(R.string.previous_task)
            val nextTaskLabel: String = stringResource(R.string.next_task)

            IconButton(
                onClick = { onPageSelected(selectedPage.previous()) },
                modifier = Modifier.semantics {
                    contentDescription = previousTaskLabel
                }
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
                    contentDescription = null
                )
            }

            Text("Task ${selectedPage.ordinal + 1} / ${TaskScreen.entries.lastIndex + 1}")

            IconButton(
                onClick = { onPageSelected(selectedPage.next()) },
                modifier = Modifier.semantics { contentDescription = nextTaskLabel }
            ) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowForward),
                    contentDescription = null
                )
            }
        }
        Text(text = selectedPage.label)
        HorizontalDivider()

        selectedPage.composable()
    }
}

/**
 * This the key which is used to store the `startFromStep` [String] of the [TaskScreen.id] of the
 * [TaskScreen] that our app should start from in the [Intent] that lauches our app.
 */
const val EXTRA_START_TASK: String = "EXTRA_START_TASK"

/**
 * This enum is used to control which "task" is displayed by [PerformanceCodeLabScreen].
 *
 * @param id the [String] that identifies the [TaskScreen].
 * @param label a [String] that [PerformanceCodeLabScreen] can use to "label" the [TaskScreen].
 * @param composable a Composable lambda which [PerformanceCodeLabScreen] should compose when the
 * [TaskScreen] is the selected one.
 */
private enum class TaskScreen(
    val id: String,
    val label: String,
    val composable: @Composable () -> Unit
) {
    /**
     * [TaskScreen] used to display the [AccelerateHeavyScreen] Composable
     */
    AccelerateHeavyScreen(
        id = "accelerate_heavy",
        label = "Accelerate - HeavyScreen",
        composable = { AccelerateHeavyScreen() }
    ),

    /**
     * [TaskScreen] used to display the [PhasesComposeLogo] Composable
     */
    PhasesLogo(
        id = "phases_logo",
        label = "Phases - Compose Logo",
        composable = { PhasesComposeLogo() }
    ),

    /**
     * [TaskScreen] used to display the [PhasesAnimatedShape] Composable
     */
    PhasesAnimatedShape(
        id = "phases_animatedshape",
        label = "Phases - Animating Shape",
        composable = { PhasesAnimatedShape() }
    ),

    /**
     * [TaskScreen] used to display the [StabilityScreen] Composable
     */
    StabilityList(
        id = "stability_screen",
        label = "Stability - Stable LazyList",
        composable = { StabilityScreen() }
    );

    /**
     * Convenience property to check if `this` [TaskScreen] is the first of the four. Returns `true`
     * if the [ordinal] of this [TaskScreen] is equal to 0.
     */
    val isFirst get() = ordinal == 0

    /**
     * Convenience property to retrieve the [TaskScreen] whose [ordinal] is one less then `this`
     * [TaskScreen].
     */
    fun previous() = entries[Math.floorMod(ordinal - 1, entries.size)]

    /**
     * Convenience property to retrieve the [TaskScreen] whose [ordinal] is one more then `this`
     * [TaskScreen].
     */
    fun next() = entries[Math.floorMod(ordinal + 1, entries.size)]

    companion object {
        /**
         * Returns the [TaskScreen] whose [TaskScreen.id] is equal to our [String] parameter [extra]
         * defaulting to the "first" [TaskScreen] in the enum declaration ([AccelerateHeavyScreen]).
         *
         * @param extra the [String] we are to search the [TaskScreen.id]'s of the [TaskScreen] for.
         * @return the [TaskScreen] whose [TaskScreen.id] is equal to [String] parameter [extra] or
         * [AccelerateHeavyScreen] if none match.
         */
        fun from(extra: String?) = entries.firstOrNull { it.id == extra } ?: entries.first()
    }
}
