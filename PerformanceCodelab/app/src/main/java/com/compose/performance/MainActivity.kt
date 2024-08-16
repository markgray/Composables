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
     * Called when the activity is starting. First we call our super's implementation of `onCreate`
     * then we call [enableEdgeToEdge] to enable edge-to-edge display. We call [setContent] to have
     * it Compose its `content` composable lambda argument into our activity. In that lambda we
     * initialize our [PerformanceCodeLabViewModel] variable `val viewModel` to an existing instance
     * or a new instance if there is none whose `startFromStep` argument is the [String] stored under
     * the key [EXTRA_START_TASK] ("EXTRA_START_TASK") that we have retrieved from the [Intent] that
     * started the app to initialize our [String] variable `val startFromStep`. Then wrapped in our
     * [PerformanceWorkshopTheme] custom [MaterialTheme] we compose a [Surface] whose `modifier`
     * argument is a [Modifier.fillMaxSize] to have it take up its entire incoming size constraints,
     * with a [Modifier.semantics] chained to that sets the [SemanticsPropertyReceiver.testTagsAsResourceId]
     * property to `true` to map testTags to resource-id to make them available to tests. The
     * background `color` argument of the [Surface] is the [ColorScheme.background] of our custom
     * [MaterialTheme.colorScheme]. In the `content` composable lambda argument we compose our
     * [PerformanceCodeLabScreen] composable with its `selectedPage` argument the [MutableState.value]
     * of the [MutableState] wrapped [TaskScreen] property [PerformanceCodeLabViewModel.selectedPage]
     * of our [PerformanceCodeLabViewModel] variable `viewModel`, and with its `onPageSelected`
     * argument a lambda which accepts the [TaskScreen] passed it in the variable `taskScreen`, then
     * sets the [MutableState.value] of the [MutableState] wrapped [TaskScreen] property
     * [PerformanceCodeLabViewModel.selectedPage] of our [PerformanceCodeLabViewModel] variable
     * `viewModel` to the [TaskScreen] in the `taskScreen` variable.
     *
     * @param savedInstanceState we do not override [onSaveInstanceState] so do not use this.
     */
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
 *  center the horizontal arrangement its children. In the [RowScope] `content`
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
            val previousTaskLabel = stringResource(R.string.previous_task)
            val nextTaskLabel = stringResource(R.string.next_task)

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

const val EXTRA_START_TASK = "EXTRA_START_TASK"

private enum class TaskScreen(
    val id: String,
    val label: String,
    val composable: @Composable () -> Unit
) {
    AccelerateHeavyScreen(
        id = "accelerate_heavy",
        label = "Accelerate - HeavyScreen",
        composable = { AccelerateHeavyScreen() }
    ),
    PhasesLogo(
        id = "phases_logo",
        label = "Phases - Compose Logo",
        composable = { PhasesComposeLogo() }
    ),
    PhasesAnimatedShape(
        id = "phases_animatedshape",
        label = "Phases - Animating Shape",
        composable = { PhasesAnimatedShape() }
    ),
    StabilityList(
        id = "stability_screen",
        label = "Stability - Stable LazyList",
        composable = { StabilityScreen() }
    );

    val isFirst get() = ordinal == 0

    fun previous() = entries[Math.floorMod(ordinal - 1, entries.size)]
    fun next() = entries[Math.floorMod(ordinal + 1, entries.size)]

    companion object {
        fun from(extra: String?) = entries.firstOrNull { it.id == extra } ?: entries.first()
    }
}
