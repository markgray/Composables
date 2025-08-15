/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("ktlint:standard:max-line-length")

package com.google.samples.apps.nowinandroid.feature.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.ui.PreviewParameterData.newsResources
import com.google.samples.apps.nowinandroid.core.ui.PreviewParameterData.topics

/**
 * This [PreviewParameterProvider](https://developer.android.com/reference/kotlin/androidx/compose/ui/tooling/preview/PreviewParameterProvider)
 * provides a [List] of [SearchResultUiState] for Composable previews.
 */
class SearchUiStatePreviewParameterProvider : PreviewParameterProvider<SearchResultUiState> {
    /**
     * A sequence of [SearchResultUiState] for Composable previews. We return the result of calling
     * the [sequenceOf] method with its `element` argument an [SearchResultUiState.Success] whose
     * `topics` argument is created by using the [Iterable.mapIndexed] method of the [topics] dummy
     * [List] of [Topic] and in the `transform` lambda argument capturing the index of the [Topic]
     * in the [Int] variable `i` and the [Topic] itself in the [Topic] variable `topic`. We then
     * create a [FollowableTopic] whose `topic` property is `topic` and whose `isFollowed` property
     * is `true` if `i` is even, and `false` otherwise. The `newsResources` property of the
     * [SearchResultUiState.Success] is the [newsResources] dummy [List] of  [UserNewsResource].
     */
    override val values: Sequence<SearchResultUiState> = sequenceOf(
        element = SearchResultUiState.Success(
            topics = topics.mapIndexed { i: Int, topic: Topic ->
                FollowableTopic(topic = topic, isFollowed = i % 2 == 0)
            },
            newsResources = newsResources,
        ),
    )
}
