/*
 * Copyright 2021 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.feature.interests

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaLoadingWheel
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.ui.DevicePreviews
import com.google.samples.apps.nowinandroid.core.ui.FollowableTopicPreviewParameterProvider
import com.google.samples.apps.nowinandroid.core.ui.TrackScreenViewEvent
import kotlinx.coroutines.flow.StateFlow

/**
 * Displays the [InterestsScreen], hoisting the [InterestsUiState] so that [InterestsScreen] can
 * be stateless.
 *
 * We initialize our [State] wrapped [InterestsUiState] variable `uiState` to the instance returned
 * by the [StateFlow.collectAsStateWithLifecycle] method of the [StateFlow] of [InterestsUiState]
 * property [InterestsViewModel.uiState] of our [InterestsViewModel] parameter [viewModel]. Then
 * we compose an [InterestsScreen] with the arguments:
 *  - `uiState`: our [State] wrapped [InterestsUiState] variable `uiState`.
 *  - `followTopic`: the [InterestsViewModel.followTopic] method of our [InterestsViewModel] parameter
 *  [viewModel].
 *  - `onTopicClick`: a lambda that accepts the [String] passed the lambda in variable `topicId` then
 *  calls the [InterestsViewModel.onTopicClick] method of our [InterestsViewModel] parameter
 *  [viewModel] with `topicId` as its `topicId` argument, and also calls our [onTopicClick] lambda
 *  parameter with `topicId`
 *  - `shouldHighlightSelectedTopic`: our [Boolean] parameter [shouldHighlightSelectedTopic].
 *  - `modifier`: our [Modifier] parameter [modifier].
 *
 * @param onTopicClick Called when a topic is clicked.
 * @param modifier Modifier to be applied to the InterestsScreen.
 * @param shouldHighlightSelectedTopic Whether the selected topic should be highlighted.
 * @param viewModel ViewModel that handles the business logic of this screen.
 */
@Composable
fun InterestsRoute(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    shouldHighlightSelectedTopic: Boolean = false,
    viewModel: InterestsViewModel = hiltViewModel(),
) {
    val uiState: InterestsUiState by viewModel.uiState.collectAsStateWithLifecycle()

    InterestsScreen(
        uiState = uiState,
        followTopic = viewModel::followTopic,
        onTopicClick = { topicId: String ->
            viewModel.onTopicClick(topicId = topicId)
            onTopicClick(topicId)
        },
        shouldHighlightSelectedTopic = shouldHighlightSelectedTopic,
        modifier = modifier,
    )
}

/**
 * Displays the Interests screen.
 *
 * Our root composable is a [Column] whose `modifier` argument is our [Modifier] parameter [modifier]
 * and whose `horizontalAlignment` argument is [Alignment.CenterHorizontally]. In the [ColumnScope]
 * `content` composable lambda argument we branch on the value of our [InterestsUiState] parameter
 * [uiState]:
 *  - If its [InterestsUiState.Loading] we display a [NiaLoadingWheel] with its `contentDesc`
 *  argument the [String] with resource ID `R.string.feature_interests_loading` ("Loading data")
 *  - If its [InterestsUiState.Interests] we display a [TopicsTabContent] whose `topics` argument
 *  is the [List] of [FollowableTopic]s returned by the [InterestsUiState.Interests.topics] property,
 *  whose `onTopicClick` argument is our [onTopicClick] lambda parameter, whose `onFollowButtonClick`
 *  argument is our [followTopic] lambda parameter, whose `selectedTopicId` argument is the [String]
 *  returned by the [InterestsUiState.Interests.selectedTopicId] property, and whose
 *  `shouldHighlightSelectedTopic` argument is our [Boolean] parameter [shouldHighlightSelectedTopic].
 *  - If its [InterestsUiState.Empty] we display a [InterestsEmptyScreen].
 *
 * Below the [Column] we call the [TrackScreenViewEvent] method with its `screenName` argument the
 * [String] "Interests".
 *
 * @param uiState The current Interests UI state.
 * @param followTopic A lambda function to call when a topic is followed or unfollowed.
 * @param onTopicClick A lambda function to call when a topic is clicked.
 * @param modifier The modifier for the screen.
 * @param shouldHighlightSelectedTopic Whether to highlight the selected topic.
 */
@Composable
internal fun InterestsScreen(
    uiState: InterestsUiState,
    followTopic: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    shouldHighlightSelectedTopic: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            InterestsUiState.Loading ->
                NiaLoadingWheel(
                    contentDesc = stringResource(id = R.string.feature_interests_loading),
                )

            is InterestsUiState.Interests ->
                TopicsTabContent(
                    topics = uiState.topics,
                    onTopicClick = onTopicClick,
                    onFollowButtonClick = followTopic,
                    selectedTopicId = uiState.selectedTopicId,
                    shouldHighlightSelectedTopic = shouldHighlightSelectedTopic,
                )

            is InterestsUiState.Empty -> InterestsEmptyScreen()
        }
    }
    TrackScreenViewEvent(screenName = "Interests")
}

/**
 * Displays a message explaining that there are no topics to display.
 *
 * It composes a [Text] whose `text` argument is the [String] with resource ID
 * [R.string.feature_interests_empty_header] ("No available data").
 */
@Composable
private fun InterestsEmptyScreen() {
    Text(text = stringResource(id = R.string.feature_interests_empty_header))
}

/**
 * Preview of the [InterestsScreen] when populated with a list of followable topics.
 *
 * It is wrapped in our custom [NiaTheme] themed [NiaBackground] Composable. Its root Composable
 * is an [InterestsScreen] whose `uiState` argument is an [InterestsUiState.Interests] whose
 * `selectedTopicId` argument is `null` and whose `topics` argument is our [List] of [FollowableTopic]
 * parameter [followableTopics]. Its `followTopic` argument is a no-op lambda, and its `onTopicClick`
 * argument is a no-op lambda.
 *
 * The @[DevicePreviews] annotation causes this Composable to be rendered on different devices in
 * the Android Studio Preview pane. The @[PreviewParameter] annotation on our [followableTopics]
 * parameter causes the Preview to use values provided by the `values` [Sequence] of the
 * [FollowableTopicPreviewParameterProvider] class ([FollowableTopicPreviewParameterProvider.values]).
 *
 * @param followableTopics the [List] of [FollowableTopic] to display, this is provided by our
 * [FollowableTopicPreviewParameterProvider] class.
 */
@DevicePreviews
@Composable
fun InterestsScreenPopulated(
    @PreviewParameter(provider = FollowableTopicPreviewParameterProvider::class)
    followableTopics: List<FollowableTopic>,
) {
    NiaTheme {
        NiaBackground {
            InterestsScreen(
                uiState = InterestsUiState.Interests(
                    selectedTopicId = null,
                    topics = followableTopics,
                ),
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}

/**
 * Preview of the [InterestsScreen] when the screen is loading.
 *
 * It is wrapped in our custom [NiaTheme] themed [NiaBackground] Composable. Its root Composable
 * is an [InterestsScreen] whose `uiState` argument is [InterestsUiState.Loading], whose `followTopic`
 * argument is a no-op lambda, and whose `onTopicClick` argument is a no-op lambda.
 *
 * The @[DevicePreviews] annotation causes this Composable to be rendered on different devices in
 * the Android Studio Preview pane.
 */
@DevicePreviews
@Composable
fun InterestsScreenLoading() {
    NiaTheme {
        NiaBackground {
            InterestsScreen(
                uiState = InterestsUiState.Loading,
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}

/**
 * Preview of the [InterestsScreen] when the UI state is empty.
 *
 * It is wrapped in our custom [NiaTheme] themed [NiaBackground] Composable. Its root Composable
 * is an [InterestsScreen] whose `uiState` argument is an [InterestsUiState.Empty], whose
 * `followTopic` argument is a no-op lambda, and its `onTopicClick` argument is a no-op lambda.
 *
 * The @[DevicePreviews] annotation causes this Composable to be rendered on different devices in
 * the Android Studio Preview pane.
 */
@DevicePreviews
@Composable
fun InterestsScreenEmpty() {
    NiaTheme {
        NiaBackground {
            InterestsScreen(
                uiState = InterestsUiState.Empty,
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}
