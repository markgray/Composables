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

package com.google.samples.apps.nowinandroid.ui.interests2pane

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldPredictiveBackHandler
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.samples.apps.nowinandroid.feature.interests.InterestsRoute
import com.google.samples.apps.nowinandroid.feature.interests.navigation.InterestsRoute
import com.google.samples.apps.nowinandroid.feature.topic.TopicDetailPlaceholder
import com.google.samples.apps.nowinandroid.feature.topic.TopicScreen
import com.google.samples.apps.nowinandroid.feature.topic.TopicViewModel
import com.google.samples.apps.nowinandroid.feature.topic.navigation.TopicRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.math.max

/**
 * A placeholder route for when no topic is selected in the detail pane.
 * This is used to distinguish between the case where no topic is selected and when a specific
 * topic is selected, allowing for different UI to be displayed in the detail pane.
 */
@Serializable
internal object TopicPlaceholderRoute

/**
 * Adds the [InterestsListDetailScreen] to the navigation graph.
 * This screen is a two-pane layout that displays a list of interests on the left and the details
 * of the selected interest on the right.
 */
fun NavGraphBuilder.interestsListDetailScreen() {
    composable<InterestsRoute> {
        InterestsListDetailScreen()
    }
}

/**
 * The main screen for displaying a list of interests and the details of a selected interest.
 * This composable function collects the selected topic ID from the view model and passes it to its
 * stateless overload.
 *
 * We start by initializing our [State] wrapped [String] variable `selectedTopicId` to the value
 * returned by the [StateFlow.collectAsStateWithLifecycle] method of the
 * [Interests2PaneViewModel.selectedTopicId] property of our [Interests2PaneViewModel] parameter
 * [viewModel]. Then we call the stateless `InterestsListDetailScreen` overload with its arguments:
 *  - `selectedTopicId`: The ID of the selected topic, or `null` if no topic is selected, our
 *  [State] wrapped [String] variable `selectedTopicId`.
 *  - `onTopicClick`: A function to be called when a topic is clicked, the
 *  [Interests2PaneViewModel.onTopicClick] method of our [Interests2PaneViewModel] parameter
 *  [viewModel]
 *  - `windowAdaptiveInfo`: Information about the window size and display features, our
 *  [WindowAdaptiveInfo] parameter [windowAdaptiveInfo].
 *
 * @param viewModel The view model for managing the state of the interests screen.
 * @param windowAdaptiveInfo Information about the window size and display features.
 */
@Composable
internal fun InterestsListDetailScreen(
    viewModel: Interests2PaneViewModel = hiltViewModel(),
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val selectedTopicId: String? by viewModel.selectedTopicId.collectAsStateWithLifecycle()
    InterestsListDetailScreen(
        selectedTopicId = selectedTopicId,
        onTopicClick = viewModel::onTopicClick,
        windowAdaptiveInfo = windowAdaptiveInfo,
    )
}

/**
 * This is the stateless version of the [InterestsListDetailScreen] composable.
 *
 * It uses a [NavigableListDetailPaneScaffold] to display two panes:
 *  - The list pane displays the [InterestsRoute] composable.
 *  - The detail pane displays either a [TopicScreen] for the selected topic or a
 *  [TopicDetailPlaceholder] if no topic is selected.
 *
 * The [PaneExpansionState] is used to control the expansion and collapse of the panes, and a
 * [VerticalDragHandle] is provided to allow the user to resize the panes.
 *
 * Back navigation is handled by a [ThreePaneScaffoldPredictiveBackHandler] and a [BackHandler]
 * to ensure that the panes are collapsed or navigated back as expected.
 *
 * @param selectedTopicId The ID of the currently selected topic, or `null` if no topic is selected.
 * @param onTopicClick A lambda function that is called when a topic is clicked in the list pane.
 * @param windowAdaptiveInfo Information about the window size and display features, used to
 * configure the scaffold directive.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun InterestsListDetailScreen(
    selectedTopicId: String?,
    onTopicClick: (String) -> Unit,
    windowAdaptiveInfo: WindowAdaptiveInfo,
) {
    /**
     * The [ThreePaneScaffoldNavigator] that manages the navigation between the list and detail
     * of our [NavigableListDetailPaneScaffold]
     */
    val listDetailNavigator: ThreePaneScaffoldNavigator<Nothing> =
        rememberListDetailPaneScaffoldNavigator(
            scaffoldDirective = calculatePaneScaffoldDirective(windowAdaptiveInfo = windowAdaptiveInfo),
            initialDestinationHistory = listOfNotNull(
                ThreePaneScaffoldDestinationItem(ListDetailPaneScaffoldRole.List),
                ThreePaneScaffoldDestinationItem<Nothing>(ListDetailPaneScaffoldRole.Detail).takeIf {
                    selectedTopicId != null
                },
            ),
        )

    /**
     * The [CoroutineScope] that we use to launch coroutines in the [BackHandler].
     */
    val coroutineScope: CoroutineScope = rememberCoroutineScope()

    /**
     * The [PaneExpansionState] that we use to control the expansion and collapse of the panes.
     */
    val paneExpansionState: PaneExpansionState = rememberPaneExpansionState(
        anchors = listOf(
            PaneExpansionAnchor.Proportion(0f),
            PaneExpansionAnchor.Proportion(0.5f),
            PaneExpansionAnchor.Proportion(1f),
        ),
    )

    ThreePaneScaffoldPredictiveBackHandler(
        navigator = listDetailNavigator,
        backBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange,
    )
    BackHandler(
        enabled = paneExpansionState.currentAnchor == PaneExpansionAnchor.Proportion(0f) &&
            listDetailNavigator.isListPaneVisible() &&
            listDetailNavigator.isDetailPaneVisible(),
    ) {
        coroutineScope.launch {
            paneExpansionState.animateTo(anchor = PaneExpansionAnchor.Proportion(proportion = 1f))
        }
    }

    /**
     * The [MutableState] wrapped [Any] that we use to store the route for the selected topic.
     */
    var topicRoute: Any by remember {
        val route = selectedTopicId?.let { TopicRoute(id = it) } ?: TopicPlaceholderRoute
        mutableStateOf(route)
    }

    /**
     * Handles the click event for a topic in the list pane.
     *
     * This function performs the following actions:
     *  1. Calls the `onTopicClick` lambda passed from the parent composable to update the selected
     *  topic ID in the view model.
     *  2. Updates the `topicRoute` state variable to navigate to the selected topic's detail screen.
     *  3. Launches a coroutine to navigate the `listDetailNavigator` to the detail pane.
     *  4. If the detail pane is currently fully expanded (covering the list pane), it launches
     *  another coroutine to animate the pane expansion state to reveal the list pane alongside
     *  the detail pane (typically a 50/50 split).
     *
     * @param topicId The ID of the topic that was clicked.
     */
    fun onTopicClickShowDetailPane(topicId: String) {
        onTopicClick(topicId)
        topicRoute = TopicRoute(id = topicId)
        coroutineScope.launch {
            listDetailNavigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
        }
        if (paneExpansionState.currentAnchor == PaneExpansionAnchor.Proportion(proportion = 1f)) {
            coroutineScope.launch {
                paneExpansionState.animateTo(anchor = PaneExpansionAnchor.Proportion(proportion = 0f))
            }
        }
    }

    /**
     * The [MutableInteractionSource] for observing and emitting Interactions for the
     * [VerticalDragHandle] of our [NavigableListDetailPaneScaffold].
     */
    val mutableInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() }

    /**
     * The minimum width of the list pane.
     */
    val minPaneWidth: Dp = 300.dp

    NavigableListDetailPaneScaffold(
        navigator = listDetailNavigator,
        listPane = {
            AnimatedPane {
                Box(
                    modifier = Modifier
                        .clipToBounds()
                        .layout { measurable: Measurable, constraints: Constraints ->
                            val width: Int = max(minPaneWidth.roundToPx(), constraints.maxWidth)
                            val placeable: Placeable = measurable.measure(
                                constraints = constraints.copy(
                                    minWidth = minPaneWidth.roundToPx(),
                                    maxWidth = width,
                                ),
                            )
                            layout(width = constraints.maxWidth, height = placeable.height) {
                                placeable.placeRelative(
                                    x = 0,
                                    y = 0,
                                )
                            }
                        },
                ) {
                    InterestsRoute(
                        onTopicClick = ::onTopicClickShowDetailPane,
                        shouldHighlightSelectedTopic = listDetailNavigator.isDetailPaneVisible(),
                    )
                }
            }
        },
        detailPane = {
            AnimatedPane {
                Box(
                    modifier = Modifier
                        .clipToBounds()
                        .layout { measurable: Measurable, constraints: Constraints ->
                            val width: Int = max(minPaneWidth.roundToPx(), constraints.maxWidth)
                            val placeable: Placeable = measurable.measure(
                                constraints = constraints.copy(
                                    minWidth = minPaneWidth.roundToPx(),
                                    maxWidth = width,
                                ),
                            )
                            layout(width = constraints.maxWidth, height = placeable.height) {
                                placeable.placeRelative(
                                    x = constraints.maxWidth -
                                        max(constraints.maxWidth, placeable.width),
                                    y = 0,
                                )
                            }
                        },
                ) {
                    AnimatedContent(targetState = topicRoute) { route: Any ->
                        when (route) {
                            is TopicRoute -> {
                                TopicScreen(
                                    showBackButton = !listDetailNavigator.isListPaneVisible(),
                                    onBackClick = {
                                        coroutineScope.launch {
                                            listDetailNavigator.navigateBack()
                                        }
                                    },
                                    onTopicClick = ::onTopicClickShowDetailPane,
                                    viewModel = hiltViewModel<TopicViewModel, TopicViewModel.Factory>(
                                        key = route.id,
                                    ) { factory: TopicViewModel.Factory ->
                                        factory.create(topicId = route.id)
                                    },
                                )
                            }

                            is TopicPlaceholderRoute -> {
                                TopicDetailPlaceholder()
                            }
                        }
                    }
                }
            }
        },
        paneExpansionState = paneExpansionState,
        paneExpansionDragHandle = {
            VerticalDragHandle(
                modifier = Modifier.paneExpansionDraggable(
                    state = paneExpansionState,
                    minTouchTargetSize = LocalMinimumInteractiveComponentSize.current,
                    interactionSource = mutableInteractionSource,
                ),
                interactionSource = mutableInteractionSource,
            )
        },
    )
}

/**
 * Checks if the list pane is currently visible (expanded) in the scaffold.
 * This is determined by querying the `scaffoldValue` of the navigator for the
 * `ListDetailPaneScaffoldRole.List` and checking if its state is [PaneAdaptedValue.Expanded].
 *
 * @return `true` if the list pane is visible, `false` otherwise.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isListPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.List] == PaneAdaptedValue.Expanded

/**
 * Checks if the detail pane is currently visible (expanded) in the scaffold.
 * This is determined by querying the `scaffoldValue` of the navigator for the
 * `ListDetailPaneScaffoldRole.Detail` and checking if its state is [PaneAdaptedValue.Expanded].
 *
 * @return `true` if the detail pane is visible, `false` otherwise.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun <T> ThreePaneScaffoldNavigator<T>.isDetailPaneVisible(): Boolean =
    scaffoldValue[ListDetailPaneScaffoldRole.Detail] == PaneAdaptedValue.Expanded
