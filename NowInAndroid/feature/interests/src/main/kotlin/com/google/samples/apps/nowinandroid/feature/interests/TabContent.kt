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

package com.google.samples.apps.nowinandroid.feature.interests

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.ScrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.ui.InterestsItem

/**
 * Displays a list of [FollowableTopic] in a [LazyColumn].
 *
 * Our root composable is a [Box] whose `modifier` argument chains to our [Modifier] parameter
 * [modifier] a [Modifier.fillMaxWidth]. In the [BoxScope] `content` composable lambda argument of
 * the [Box] we start by initializing and remembering our [LazyListState] variable `scrollableState`
 * with a [rememberLazyListState] call. Then we compose a [LazyColumn] whose arguments are:
 *  - `modifier`: is a [Modifier.padding] that adds `24.dp` to the `horizontal` sides chained to a
 *  [Modifier.testTag] with the tag "interests:topics".
 *  - `contentPadding`: is a [PaddingValues] that adds `16.dp` to the `vertical` sides.
 *  - `state`: is our [LazyListState] variable `scrollableState`.
 *
 * In the [LazyListScope] `content` composable lambda argument of the [LazyColumn] we use the
 * [Iterable.forEach] method of our [List] of [FollowableTopic] parameter [topics] to loop through
 * its contents capturing the [FollowableTopic] in the `followableTopic` variable. We initialize our
 * [String] variable `topicId` to the [Topic.id] of the [FollowableTopic.topic] of `followableTopic`
 * and compose an [LazyListScope.item] whose `key` argument is `topicId` and in whose
 * [LazyItemScope] `content` composable lambda argument we initialize our [Boolean] variable
 * `isSelected` to `true` if our [Boolean] parameter [shouldHighlightSelectedTopic] is `true` and
 * `topicId` is equal to our [String] parameter [selectedTopicId]. Then we compose an
 * [InterestsItem] whose arguments are:
 *  - `name`: is the [Topic.name] of the [FollowableTopic.topic] of `followableTopic`.
 *  - `following`: is the [FollowableTopic.isFollowed] property of `followableTopic`.
 *  - `description`: is the [Topic.shortDescription] property of the [FollowableTopic.topic] of
 *  `followableTopic`.
 *  - `topicImageUrl`: is the [Topic.imageUrl] property of the [FollowableTopic.topic] of
 *  `followableTopic`.
 *  - `onClick`: is a lambda that calls our lambda parameter [onTopicClick] with the [String]
 *  variable `topicId` as its argument.
 *  - `onFollowButtonClick`: is a lambda that calls our lambda parameter [onFollowButtonClick] with
 *  the [String] variable `topicId` and the [Boolean] passed the lambda in variable `it` as its
 *  arguments.
 *  - `isSelected`: is our [Boolean] variable `isSelected`.
 *  - `modifier`: is a [Modifier.fillMaxWidth].
 *
 * At the bottom of the [LazyColumn] if our [Boolean] parameter [withBottomSpacer] is `true` we
 * compose an [LazyListScope.item] that holds a [Spacer] whose `modifier` argument is a
 * [Modifier.windowInsetsBottomHeight] that adds [WindowInsets.Companion.safeDrawing] to the `insets`.
 *
 * Next in the [Box] we initialize our [ScrollbarState] variable `scrollbarState` to the
 * [LazyListState.scrollbarState] of our [LazyListState] variable `scrollableState` with its
 * `itemsAvailable` the [List.size] of our [List] of [FollowableTopic] parameter [topics]. Then
 * we compose a [DraggableScrollbar] with `scrollableState` as its [ScrollableState] receiver
 * and the arguments:
 *  - `modifier`: is a [Modifier.fillMaxHeight] chained to a [Modifier.windowInsetsPadding] that
 *  adds [WindowInsets.Companion.systemBars] to the `insets`, chained to a [Modifier.padding] that
 *  adds `2.dp` to the `horizontal` sides, chained to a [BoxScope.align] whose `alignment` is
 *  [Alignment.CenterEnd].
 *  - `state`: is our [ScrollbarState] variable `scrollbarState`.
 *  - `orientation`: is [Orientation.Vertical].
 *  - `onThumbMoved`: is the [rememberDraggableScroller] returned by the
 *  [LazyListState.rememberDraggableScroller] extension function of our [LazyListState] variable
 *  `scrollableState` with its `itemsAvailable` the [List.size] of our [List] of [FollowableTopic]
 *  parameter [topics].
 *
 * @param topics The list of topics to display.
 * @param onTopicClick Called when a topic is clicked.
 * @param onFollowButtonClick Called when the follow button for a topic is clicked.
 * @param modifier Modifier to be applied to the content.
 * @param withBottomSpacer Whether to include a spacer at the bottom of the list.
 * @param selectedTopicId The ID of the currently selected topic.
 * @param shouldHighlightSelectedTopic Whether to highlight the selected topic.
 */
@Composable
fun TopicsTabContent(
    topics: List<FollowableTopic>,
    onTopicClick: (String) -> Unit,
    onFollowButtonClick: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    withBottomSpacer: Boolean = true,
    selectedTopicId: String? = null,
    shouldHighlightSelectedTopic: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        val scrollableState: LazyListState = rememberLazyListState()
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .testTag(tag = "interests:topics"),
            contentPadding = PaddingValues(vertical = 16.dp),
            state = scrollableState,
        ) {
            topics.forEach { followableTopic: FollowableTopic ->
                val topicId: String = followableTopic.topic.id
                item(key = topicId) {
                    val isSelected: Boolean =
                        shouldHighlightSelectedTopic && topicId == selectedTopicId
                    InterestsItem(
                        name = followableTopic.topic.name,
                        following = followableTopic.isFollowed,
                        description = followableTopic.topic.shortDescription,
                        topicImageUrl = followableTopic.topic.imageUrl,
                        onClick = { onTopicClick(topicId) },
                        onFollowButtonClick = { onFollowButtonClick(topicId, it) },
                        isSelected = isSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            if (withBottomSpacer) {
                item {
                    Spacer(
                        modifier = Modifier.windowInsetsBottomHeight(
                            insets = WindowInsets.safeDrawing,
                        ),
                    )
                }
            }
        }
        val scrollbarState: ScrollbarState = scrollableState.scrollbarState(
            itemsAvailable = topics.size,
        )
        scrollableState.DraggableScrollbar(
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(insets = WindowInsets.systemBars)
                .padding(horizontal = 2.dp)
                .align(alignment = Alignment.CenterEnd),
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = scrollableState.rememberDraggableScroller(
                itemsAvailable = topics.size,
            ),
        )
    }
}
