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
import kotlinx.coroutines.CoroutineScope
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
     * Represents the state of the topic information, including whether it is followed. We use the
     * [Flow.stateIn] method of the [Flow] of [TopicUiState] that our [topicUiState] method returns
     * when called with its `topicId` argument our [String] property [topicId], its
     * `userDataRepository` argument our [UserDataRepository] property [userDataRepository], and its
     * `topicsRepository` argument our [TopicsRepository] property [topicsRepository] to convert it
     * to a [StateFlow] of [TopicUiState] whose [CoroutineScope] `scope` argument is [viewModelScope],
     * whose `started` argument is [SharingStarted.WhileSubscribed] for a `stopTimeoutMillis` of
     * 5,000 milliseconds, and whose `initialValue` argument is [TopicUiState.Loading].
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

    /**
     * Represents the state of the news articles related to the current topic. This StateFlow emits
     * a new value whenever the list of news articles changes, or when an error occurs while
     * fetching the news. The initial value is [NewsUiState.Loading], indicating that the news is
     * currently being loaded. We use the [Flow.stateIn] method of the [Flow] of [NewsUiState] that
     * our [newsUiState] method returns when called with its `topicId` argument our [String]
     * property [topicId], its `userDataRepository` argument our [UserDataRepository] property
     * [userDataRepository], and its `userNewsResourceRepository` argument our
     * [UserNewsResourceRepository] property [userNewsResourceRepository] to convert it to a
     * [StateFlow] of [NewsUiState] whose [CoroutineScope] `scope` argument is our [viewModelScope],
     * whose `started` argument is [SharingStarted.WhileSubscribed] for a `stopTimeoutMillis` of
     * 5,000 milliseconds, and whose `initialValue` argument is [NewsUiState.Loading].
     */
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

    /**
     * Toggles the follow state of the current topic. We launch a coroutine in the [viewModelScope]
     * using its [CoroutineScope.launch] method and in its [CoroutineScope] `block` lambda argument
     * we call the [UserDataRepository.setTopicIdFollowed] method of our [UserDataRepository]
     * property [userDataRepository] with its `followedTopicId` argument our [String] property
     * [topicId] and its `followed` argument our [Boolean] parameter [followed].
     *
     * @param followed A boolean indicating whether the topic should be followed or unfollowed.
     */
    fun followTopicToggle(followed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(followedTopicId = topicId, followed = followed)
        }
    }

    /**
     * Toggles the bookmark state of a news resource. We launch a coroutine in the [viewModelScope]
     * using its [CoroutineScope.launch] method and in its [CoroutineScope] `block` lambda argument
     * we call the [UserDataRepository.setNewsResourceBookmarked] method of our [UserDataRepository]
     * property [userDataRepository] with its `newsResourceId` argument our [String] parameter
     * [newsResourceId] and its `bookmarked` argument our [Boolean] parameter [bookmarked].
     *
     * @param newsResourceId The ID of the news resource to bookmark or unbookmark.
     * @param bookmarked `true` to bookmark the news resource, `false` to unbookmark it.
     */
    fun bookmarkNews(newsResourceId: String, bookmarked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceBookmarked(
                newsResourceId = newsResourceId,
                bookmarked = bookmarked,
            )
        }
    }

    /**
     * Sets the viewed state of a news resource. We launch a coroutine in the [viewModelScope]
     * using its [CoroutineScope.launch] method and in its [CoroutineScope] `block` lambda argument
     * we call the [UserDataRepository.setNewsResourceViewed] method of our [UserDataRepository]
     * property [userDataRepository] with its `newsResourceId` argument our [String] parameter
     * [newsResourceId] and its `viewed` argument our [Boolean] parameter [viewed].
     *
     * @param newsResourceId The ID of the news resource to mark as viewed or unviewed.
     * @param viewed `true` to mark the news resource as viewed, `false` to mark it as unviewed.
     */
    fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(
                newsResourceId = newsResourceId,
                viewed = viewed,
            )
        }
    }

    /**
     * Factory for creating [TopicViewModel] instances.
     */
    @AssistedFactory
    interface Factory {
        /**
         * Creates a [TopicViewModel] with the given [topicId]. Hilt will inject all other
         * dependencies that it requires. The [topicId] argument is the [String] id of the
         * [Topic] that the [TopicViewModel] is supposed to handle.
         *
         * @param topicId the ID of the [Topic] to display.
         * @return a [TopicViewModel] instance.
         */
        fun create(
            topicId: String,
        ): TopicViewModel
    }
}

/**
 * Creates a [Flow] of [TopicUiState] for a given [topicId].
 *
 * This function observes the user's followed topics and the information for the specified topic.
 * It combines these two streams of data and transforms them into a [TopicUiState] object.
 * The resulting [Flow] emits a new [TopicUiState] whenever either the followed topics or the
 * topic information changes.
 *
 * The `transform` function in the `combine` operator creates a [Pair] of the latest values from
 * `followedTopicIds` (a [Set] of [String]) and `topicStream` (a [Topic]).
 *
 * The `asResult()` operator converts the [Flow] of [Pair] into a [Flow] of [Result], which
 * represents the different states of the data loading process ([Result.Success], [Result.Loading],
 * [Result.Error]).
 *
 * The `map` operator then transforms the [Result] into a [TopicUiState].
 * - If the `Result` is `Success`, it creates a [TopicUiState.Success] with a [FollowableTopic]
 *   object, which includes the topic information and whether it is followed by the user.
 * - If the `Result` is `Loading`, it creates a [TopicUiState.Loading].
 * - If the `Result` is `Error`, it creates a [TopicUiState.Error].
 *
 * @param topicId The ID of the topic to observe.
 * @param userDataRepository The repository for user data.
 * @param topicsRepository The repository for topics.
 * @return A [Flow] of [TopicUiState] representing the current state of the topic information.
 */
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

/**
 * Returns a [Flow] of [NewsUiState] for the given [topicId].
 *
 * We start by initializing our [Flow] of [List] of [UserNewsResource] variable `newsStream` to
 * the result of calling the [UserNewsResourceRepository.observeAll] method of our
 * [UserNewsResourceRepository] parameter [userNewsResourceRepository] with its `query` argument
 * a [NewsResourceQuery] object with its `filterTopicIds` argument a [Set] of [String] containing
 * only our [String] parameter [topicId] (retrieves all the [UserNewsResource] objects that have
 * a [Topic] with the specified [topicId]). We initialize our [Flow] of [Set] of [String] variable
 * `bookmark` to the result of calling the [UserDataRepository.userData] property of our
 * [UserDataRepository] parameter [userDataRepository] then using its [Flow.map] operator to emit
 * a [Flow] of the [UserData.bookmarkedNewsResources] property of the [UserData] object passed the
 * lambda. We then combine our [Flow] of [List] of [UserNewsResource] variable `newsStream` with
 * our [Flow] of [Set] of [String] variable `bookmark` using the [combine] operator with its
 * `transform` argument a reference to [Pair] to create a [Flow] of [Pair] of [List] of
 * [UserNewsResource] and [Set] of [String]. We then use the [asResult] extension function to
 * convert our [Flow] of [Pair] into a [Flow] of [Result]. We then use its [Flow.map] operator
 * and in the `transform` lambda argument we capture the current [Result] in variable
 * `newsToBookmarksResult` then branch on its type:
 *  - If the [Result] is [Result.Success], we return a [NewsUiState.Success] object with its
 *  `news` argument the [Pair.first] of the [Result.Success.data] of `newsToBookmarksResult`
 *  - If the [Result] is [Result.Loading], we return a [NewsUiState.Loading] object.
 *  - If the [Result] is [Result.Error], we return a [NewsUiState.Error] object.
 *
 * @param topicId The ID of the topic to observe news for.
 * @param userNewsResourceRepository The repository for user news resources.
 * @param userDataRepository The repository for user data.
 * @return A [Flow] of [NewsUiState] objects.
 */
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

/**
 * A sealed interface which represents the UI state for the topic screen.
 * It can be in one of three states:
 * - [Success]: The topic data has been successfully loaded, and the `followableTopic` property
 *   contains the [FollowableTopic] to display.
 * - [Error]: An error occurred while loading the topic data.
 * - [Loading]: The topic data is currently being loaded.
 */
sealed interface TopicUiState {
    /**
     * Represents the successful state of the topic screen, containing the followable topic data.
     *
     * @property followableTopic The [FollowableTopic] to display.
     */
    data class Success(val followableTopic: FollowableTopic) : TopicUiState

    /**
     * Represents an error state where the topic data could not be loaded. This could be due to
     * network issues, server errors, or other problems. The UI should display an appropriate
     * error message to the user when this state is encountered.
     */
    data object Error : TopicUiState

    /**
     * Represents a loading state where the topic data is currently being fetched. This state is
     * typically displayed when the screen is first loaded or when the data is being refreshed.
     * The UI should show a loading indicator to inform the user that data is being retrieved.
     */
    data object Loading : TopicUiState
}

/**
 * A sealed interface which represents the UI state for the news screen.
 * It can be in one of three states:
 * - [Success]: The news data has been successfully loaded, and the `news` property
 *   contains the list of [UserNewsResource] objects to display.
 * - [Error]: An error occurred while loading the news data.
 * - [Loading]: The news data is currently being loaded.
 */
sealed interface NewsUiState {
    /**
     * Represents the successful state of the news screen, containing the list of news articles.
     *
     * @property news The list of [UserNewsResource] objects to display. Each [UserNewsResource]
     * represents a news article with its associated user-specific data, such as whether it has
     * been bookmarked or viewed.
     */
    data class Success(val news: List<UserNewsResource>) : NewsUiState

    /**
     * Represents an error state where the news data could not be loaded. This could be due to
     * network issues, server errors, or other problems. The UI should display an appropriate
     * error message to the user when this state is encountered.
     */
    data object Error : NewsUiState

    /**
     * Represents a loading state where the news data is currently being fetched. This state is
     * typically displayed when the screen is first loaded or when the data is being refreshed.
     * The UI should show a loading indicator to inform the user that data is being retrieved.
     */
    data object Loading : NewsUiState
}
