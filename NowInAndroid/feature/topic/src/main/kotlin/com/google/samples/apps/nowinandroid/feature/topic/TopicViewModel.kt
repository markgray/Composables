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

package com.google.samples.apps.nowinandroid.feature.topic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.repository.NewsResourceQuery
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.result.Result
import com.google.samples.apps.nowinandroid.core.result.asResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the topic screen, responsible for exposing the data and business logic for the UI.
 * It is Hilt-assisted, meaning it can receive dependencies from the Hilt dependency graph,
 * as well as non-Hilt dependencies passed in at runtime.
 *
 * This ViewModel exposes two StateFlows:
 * - `topicUiState`: Represents the state of the topic information, including whether it is followed.
 * - `newsUiState`: Represents the state of the news articles related to the current topic.
 *
 * It also provides methods for:
 * - Toggling the follow state of the current topic.
 * - Bookmarking or unbookmarking a news resource.
 * - Marking a news resource as viewed.
 *
 * The `Factory` interface is used by Hilt to create instances of this ViewModel,
 * allowing the `topicId` to be passed in at creation time.
 *
 * @property userDataRepository The repository for user data, injected by Hilt.
 * @property topicsRepository The repository for topics, injected by Hilt.
 * @property userNewsResourceRepository The repository for user news resources, injected by Hilt.
 * @property topicId The ID of the topic to display, injected by Hilt with an assist from our
 * [TopicViewModel.Factory].
 */
@HiltViewModel(assistedFactory = TopicViewModel.Factory::class)
class TopicViewModel @AssistedInject constructor(
    private val userDataRepository: UserDataRepository,
    topicsRepository: TopicsRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
    @Assisted val topicId: String,
) : ViewModel() {
    /**
     * Represents the state of the topic information, including whether it is followed.
     * TODO: Continue here.
     */
    val topicUiState: StateFlow<TopicUiState> = topicUiState(
        topicId = topicId,
        userDataRepository = userDataRepository,
        topicsRepository = topicsRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = TopicUiState.Loading,
        )

    val newsUiState: StateFlow<NewsUiState> = newsUiState(
        topicId = topicId,
        userDataRepository = userDataRepository,
        userNewsResourceRepository = userNewsResourceRepository,
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = NewsUiState.Loading,
        )

    fun followTopicToggle(followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(followedTopicId = topicId, followed = followed)
        }
    }

    fun bookmarkNews(newsResourceId: String, bookmarked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceBookmarked(
                newsResourceId = newsResourceId,
                bookmarked = bookmarked,
            )
        }
    }

    fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(
                newsResourceId = newsResourceId,
                viewed = viewed,
            )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            topicId: String,
        ): TopicViewModel
    }
}

private fun topicUiState(
    topicId: String,
    userDataRepository: UserDataRepository,
    topicsRepository: TopicsRepository,
): Flow<TopicUiState> {
    // Observe the followed topics, as they could change over time.
    val followedTopicIds: Flow<Set<String>> =
        userDataRepository.userData
            .map { userData: UserData -> userData.followedTopics }

    // Observe topic information
    val topicStream: Flow<Topic> = topicsRepository.getTopic(id = topicId)

    return combine(
        flow = followedTopicIds,
        flow2 = topicStream,
        transform = ::Pair,
    )
        .asResult()
        .map { followedTopicToTopicResult: Result<Pair<Set<String>, Topic>> ->
            when (followedTopicToTopicResult) {
                is Result.Success -> {
                    val (followedTopics: Set<String>, topic: Topic) =
                        followedTopicToTopicResult.data
                    TopicUiState.Success(
                        followableTopic = FollowableTopic(
                            topic = topic,
                            isFollowed = topicId in followedTopics,
                        ),
                    )
                }

                is Result.Loading -> TopicUiState.Loading
                is Result.Error -> TopicUiState.Error
            }
        }
}

private fun newsUiState(
    topicId: String,
    userNewsResourceRepository: UserNewsResourceRepository,
    userDataRepository: UserDataRepository,
): Flow<NewsUiState> {
    // Observe news
    val newsStream: Flow<List<UserNewsResource>> = userNewsResourceRepository.observeAll(
        query = NewsResourceQuery(filterTopicIds = setOf(element = topicId)),
    )

    // Observe bookmarks
    val bookmark: Flow<Set<String>> = userDataRepository.userData
        .map { userData: UserData -> userData.bookmarkedNewsResources }

    return combine(flow = newsStream, flow2 = bookmark, transform = ::Pair)
        .asResult()
        .map { newsToBookmarksResult: Result<Pair<List<UserNewsResource>, Set<String>>> ->
            when (newsToBookmarksResult) {
                is Result.Success -> NewsUiState.Success(news = newsToBookmarksResult.data.first)
                is Result.Loading -> NewsUiState.Loading
                is Result.Error -> NewsUiState.Error
            }
        }
}

sealed interface TopicUiState {
    data class Success(val followableTopic: FollowableTopic) : TopicUiState
    data object Error : TopicUiState
    data object Loading : TopicUiState
}

sealed interface NewsUiState {
    data class Success(val news: List<UserNewsResource>) : NewsUiState
    data object Error : NewsUiState
    data object Loading : NewsUiState
}
