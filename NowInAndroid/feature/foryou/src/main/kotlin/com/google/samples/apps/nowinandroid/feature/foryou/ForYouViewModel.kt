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

package com.google.samples.apps.nowinandroid.feature.foryou

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent.Param
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.data.repository.NewsResourceQuery
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.google.samples.apps.nowinandroid.core.domain.GetFollowableTopicsUseCase
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.notifications.DEEP_LINK_NEWS_RESOURCE_ID_KEY
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the For You screen.
 *
 * @param savedStateHandle The [SavedStateHandle] to use for managing state across process death.
 * @param syncManager The [SyncManager] to use for observing sync status, injected by Hilt.
 * @param analyticsHelper The [AnalyticsHelper] to use for logging analytics events, injected
 * by Hilt.
 * @param userDataRepository The [UserDataRepository] to use for managing user data, injected
 * by Hilt.
 * @param userNewsResourceRepository The [UserNewsResourceRepository] to use for observing news
 * resources, injected by Hilt.
 * @param getFollowableTopics The [GetFollowableTopicsUseCase] to use for retrieving followable
 * topics, injected by Hilt.
 */
@HiltViewModel
class ForYouViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    syncManager: SyncManager,
    private val analyticsHelper: AnalyticsHelper,
    private val userDataRepository: UserDataRepository,
    userNewsResourceRepository: UserNewsResourceRepository,
    getFollowableTopics: GetFollowableTopicsUseCase,
) : ViewModel() {

    /**
     * A [Flow] of [Boolean] that emits `true` if the onboarding screen should be shown, `false`
     * otherwise. The onboarding screen should be shown if the user has not completed it yet or
     * if they have removed all of their followed topics. We retrieve the [Flow] of [UserData]
     * from the [UserDataRepository.userData] of our [UserDataRepository] property
     * [userDataRepository] and use its [Flow.map] extension function to transform it into a [Flow] of
     * the inverse of its [Boolean] property [UserData.shouldHideOnboarding].
     */
    private val shouldShowOnboarding: Flow<Boolean> =
        userDataRepository.userData.map { !it.shouldHideOnboarding }

    /**
     * A [StateFlow] that emits the latest news resource that was deep linked into the app.
     * This is consumed by the UI to display the news resource to the user.
     * It is `null` if there is no deep linked news resource. We retrieve the [StateFlow] of
     * [String] from our [SavedStateHandle] property [savedStateHandle] with the key
     * [DEEP_LINK_NEWS_RESOURCE_ID_KEY] and the `initialValue` of `null`. We then use its
     * [Flow.flatMapLatest] extension function with the `transform` suspend lambda argument
     * capturing the [String] passed the lambda in variable `newsResourceId` and if it is `null`
     * we emit a [flowOf] an [emptyList], otherwise we use the [UserNewsResourceRepository.observeAll]
     * of our [UserNewsResourceRepository] property [userNewsResourceRepository] to retrieve the
     * [Flow] of [List] of [UserNewsResource] that match the [NewsResourceQuery] with its
     * `filterNewsIds` argument a [Set] of the [String] variable `newsResourceId`. The [Flow] emitted
     * by the [Flow.flatMapLatest] extension function is then [Flow.map]'ed to a [Flow] of the first
     * [UserNewsResource] in the [List] of [UserNewsResource]s, or `null` if the [List] is empty.
     * This [Flow] is then fed to the [Flow.stateIn] method with the `scope` argument [viewModelScope],
     * the `started` argument [SharingStarted.WhileSubscribed] and the `initialValue` argument of
     * `null` to convert it into a [StateFlow] of [UserNewsResource].
     */
    val deepLinkedNewsResource: StateFlow<UserNewsResource?> =
        savedStateHandle.getStateFlow<String?>(
            key = DEEP_LINK_NEWS_RESOURCE_ID_KEY,
            initialValue = null,
        )
            .flatMapLatest { newsResourceId: String? ->
                if (newsResourceId == null) {
                    flowOf(value = emptyList())
                } else {
                    userNewsResourceRepository.observeAll(
                        query = NewsResourceQuery(
                            filterNewsIds = setOf(newsResourceId),
                        ),
                    )
                }
            }
            .map { it.firstOrNull() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = null,
            )

    /**
     * Indicates whether the app is currently syncing data. We retrieve the [Flow] of [Boolean]
     * property [SyncManager.isSyncing] of our [SyncManager] property [syncManager] and use its
     * [Flow.stateIn] extension function to convert it into a [StateFlow] of [Boolean] with the
     * `scope` argument [viewModelScope], the `started` argument [SharingStarted.WhileSubscribed]
     * and the `initialValue` argument of `false`.
     */
    val isSyncing: StateFlow<Boolean> = syncManager.isSyncing
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = false,
        )

    /**
     * A [StateFlow] that emits the latest [NewsFeedUiState] for the For You screen.
     *
     * It is backed by the [UserNewsResourceRepository.observeAllForFollowedTopics] method of our
     * [UserNewsResourceRepository] property [userNewsResourceRepository], which returns a [Flow]
     * of [List] of [UserNewsResource]. This [Flow] is then [Flow.map]'ed to a [Flow] of
     * [NewsFeedUiState.Success] by the `tranform` suspend lambda argument [NewsFeedUiState.Success]
     * and converted to a [StateFlow] of [NewsFeedUiState] by the [Flow.stateIn] extension function
     * with the `scope` argument [viewModelScope], the `started` argument [SharingStarted.WhileSubscribed]
     * and the `initialValue` argument of [NewsFeedUiState.Loading].
     */
    val feedState: StateFlow<NewsFeedUiState> =
        userNewsResourceRepository.observeAllForFollowedTopics()
            .map(transform = NewsFeedUiState::Success)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = NewsFeedUiState.Loading,
            )

    /**
     * A [StateFlow] that emits the latest [OnboardingUiState] for the For You screen.
     *
     * It is backed by a [combine] of the [Flow] of [Boolean] property [shouldShowOnboarding] and
     * the [Flow] returned by the [GetFollowableTopicsUseCase.invoke] method of our
     * [GetFollowableTopicsUseCase] property [getFollowableTopics]. In the `transform` suspend lambda
     * argument of [combine] we capture the [Boolean] value from the [Flow] of [Boolean] property
     * [shouldShowOnboarding] in variable `shouldShowOnboarding` and the [List] of [FollowableTopic]
     * from the [Flow] of [FollowableTopic] in variable `topics`. If `shouldShowOnboarding` is `true`,
     * it emits [OnboardingUiState.Shown] with the list of topics in variable `topics`. Otherwise,
     * it emits [OnboardingUiState.NotShown]. This [Flow] of [OnboardingUiState] is then converted
     * to a [StateFlow] of [OnboardingUiState] by the [Flow.stateIn] extension function with the
     * `scope` argument [viewModelScope], the `started` argument [SharingStarted.WhileSubscribed] and
     * with an initial value of [OnboardingUiState.Loading].
     */
    val onboardingUiState: StateFlow<OnboardingUiState> =
        combine(
            flow = shouldShowOnboarding,
            flow2 = getFollowableTopics(),
        ) { shouldShowOnboarding: Boolean, topics: List<FollowableTopic> ->
            if (shouldShowOnboarding) {
                OnboardingUiState.Shown(topics = topics)
            } else {
                OnboardingUiState.NotShown
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = OnboardingUiState.Loading,
            )

    /**
     * Updates the selection of a topic. It launches a coroutine on the [viewModelScope] to call the
     * [UserDataRepository.setTopicIdFollowed] method of our [UserDataRepository] property
     * [userDataRepository] with its `followedTopicId` argument our [String] parameter [topicId] and
     * its `followed` argument our [Boolean] parameter [isChecked].
     *
     * @param topicId The ID of the topic to update.
     * @param isChecked Whether the topic is selected.
     */
    fun updateTopicSelection(topicId: String, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setTopicIdFollowed(followedTopicId = topicId, followed = isChecked)
        }
    }

    /**
     * Updates the saved state of a news resource. It launches a coroutine on the [viewModelScope]
     * to call the [UserDataRepository.setNewsResourceBookmarked] method of our
     * [UserDataRepository] property [userDataRepository] with its `newsResourceId` argument
     * our [String] parameter [newsResourceId] and its `isChecked` argument our [Boolean]
     * parameter [isChecked].
     *
     * @param newsResourceId The ID of the news resource to update.
     * @param isChecked Whether the news resource is saved.
     */
    fun updateNewsResourceSaved(newsResourceId: String, isChecked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceBookmarked(
                newsResourceId = newsResourceId,
                bookmarked = isChecked,
            )
        }
    }

    /**
     * Updates the viewed state of a news resource. It launches a coroutine on the [viewModelScope]
     * to call the [UserDataRepository.setNewsResourceViewed] method of our [UserDataRepository]
     * property [userDataRepository] with its `newsResourceId` argument our [String] parameter
     * [newsResourceId] and its `viewed` argument our [Boolean] parameter [viewed].
     *
     * @param newsResourceId The ID of the news resource to update.
     * @param viewed Whether the news resource has been viewed.
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
     * Handles the event that a deep link to a news resource was opened. If our [String] parameter
     * [newsResourceId] is the same as the [UserNewsResource.id] of the `value` of our [StateFlow]
     * of [UserNewsResource] property [deepLinkedNewsResource] we remove the value stored in our
     * [SavedStateHandle] property [savedStateHandle] under the key [DEEP_LINK_NEWS_RESOURCE_ID_KEY]
     * by setting it to `null`. We then call the [AnalyticsHelper.logNewsDeepLinkOpen] extension
     * function with its `newsResourceId` argument our [String] parameter [newsResourceId]. Finally
     * we launch a coroutine on the [viewModelScope] to call the
     * [UserDataRepository.setNewsResourceViewed] method of our [UserDataRepository] property
     * [userDataRepository] with its `newsResourceId` argument our [String] parameter
     * [newsResourceId] and its `viewed` argument `true`.
     *
     * @param newsResourceId The ID of the news resource that was opened.
     */
    fun onDeepLinkOpened(newsResourceId: String) {
        if (newsResourceId == deepLinkedNewsResource.value?.id) {
            savedStateHandle[DEEP_LINK_NEWS_RESOURCE_ID_KEY] = null
        }
        analyticsHelper.logNewsDeepLinkOpen(newsResourceId = newsResourceId)
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(
                newsResourceId = newsResourceId,
                viewed = true,
            )
        }
    }

    /**
     * Dismisses the onboarding screen. It launches a coroutine on the [viewModelScope] to call the
     * [UserDataRepository.setShouldHideOnboarding] method of our [UserDataRepository] property
     * [userDataRepository] with its `shouldHideOnboarding` argument `true`.
     */
    fun dismissOnboarding() {
        viewModelScope.launch {
            userDataRepository.setShouldHideOnboarding(shouldHideOnboarding = true)
        }
    }
}

/**
 * Logs the event that a news resource deep link was opened. We call the [AnalyticsHelper.logEvent]
 * method of our [AnalyticsHelper] receiver with an [AnalyticsEvent] with its `type` argument
 * "news_deep_link_opened" and its `extras` argument a [List] of a single [Param] with the
 * [Param] having its `key` argument "news_resource_id" and its `value` argument our
 * [String] parameter [newsResourceId].
 *
 * @param newsResourceId The ID of the news resource that was opened.
 */
private fun AnalyticsHelper.logNewsDeepLinkOpen(newsResourceId: String) =
    logEvent(
        event = AnalyticsEvent(
            type = "news_deep_link_opened",
            extras = listOf(
                Param(
                    key = DEEP_LINK_NEWS_RESOURCE_ID_KEY,
                    value = newsResourceId,
                ),
            ),
        ),
    )
