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
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent.Param
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper
import com.google.samples.apps.nowinandroid.core.data.repository.CompositeUserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.util.SyncManager
import com.google.samples.apps.nowinandroid.core.domain.GetFollowableTopicsUseCase
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.mapToUserNewsResources
import com.google.samples.apps.nowinandroid.core.notifications.DEEP_LINK_NEWS_RESOURCE_ID_KEY
import com.google.samples.apps.nowinandroid.core.testing.repository.TestNewsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestTopicsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.emptyUserData
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import com.google.samples.apps.nowinandroid.core.testing.util.TestAnalyticsHelper
import com.google.samples.apps.nowinandroid.core.testing.util.TestSyncManager
import com.google.samples.apps.nowinandroid.core.ui.NewsFeedUiState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Unit tests for [ForYouViewModel].
 *
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class ForYouViewModelTest {
    /**
     * A JUnit [TestRule] that sets the Main dispatcher to the [UnconfinedTestDispatcher] for the
     * duration of the test. It is a [TestWatcher] whose [TestWatcher.starting] override calls the
     * `Dispatchers.setMain` method with the [UnconfinedTestDispatcher], and whose [TestWatcher.finished]
     * override calls `Dispatchers.resetMain` method.
     *
     * Overrides the Dispatchers.Main used in Coroutines by the Main dispatcher controlled by the
     * rule. This allows us to use `runTest` and `advanceTimeBy` for coroutine testing.
     *
     * See https://developer.android.com/kotlin/coroutines/test#setting-main-dispatcher
     */
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    /**
     * A test version of [SyncManager] that allows directly setting the sync status.
     */
    private val syncManager = TestSyncManager()

    /**
     * A test version of [AnalyticsHelper] that records events in a list, making them available
     * for inspection and assertions.
     */
    private val analyticsHelper = TestAnalyticsHelper()

    /**
     * A test version of [UserDataRepository] that allows directly setting the user data.
     */
    private val userDataRepository = TestUserDataRepository()

    /**
     * A test version of [TopicsRepository] that allows setting the topics.
     */
    private val topicsRepository = TestTopicsRepository()

    /**
     * A test version of [NewsRepository] that allows setting the news resources.
     */
    private val newsRepository = TestNewsRepository()

    /**
     * A [CompositeUserNewsResourceRepository] that allows setting the user news resources.
     * Implements a [UserNewsResourceRepository] by combining a [NewsRepository] with a
     * [UserDataRepository].
     */
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )

    /**
     * A use case that returns a list of topics that can be followed.
     */
    private val getFollowableTopicsUseCase = GetFollowableTopicsUseCase(
        topicsRepository = topicsRepository,
        userDataRepository = userDataRepository,
    )

    /**
     * A [SavedStateHandle] for the view model.
     * SavedStateHandle is a key-value map that allows writing and
     * retrieving data to and from the saved state.
     * This map is automatically saved and restored by the framework.
     *
     * It allows us to persist data across process death and configuration changes.
     */
    private val savedStateHandle = SavedStateHandle()

    /**
     * The [ForYouViewModel] instance under test.
     * Initialized in the [setup] method.
     */
    private lateinit var viewModel: ForYouViewModel

    /**
     * Sets up the test environment by initializing the [ForYouViewModel] with mock dependencies.
     * This function is annotated with @[Before] to ensure it runs before each test method.
     * We initialize our [ForYouViewModel] property [viewModel] with a new instance with the
     * following arguments:
     *  - `syncManager`: our [TestSyncManager] property [syncManager].
     *  - `savedStateHandle`: our [SavedStateHandle] property [savedStateHandle].
     *  - `analyticsHelper`: our [TestAnalyticsHelper] property [analyticsHelper].
     *  - `userDataRepository`: our [TestUserDataRepository] property [userDataRepository].
     *  - `userNewsResourceRepository`: our [CompositeUserNewsResourceRepository] property
     *  [userNewsResourceRepository].
     *  - `getFollowableTopics`: our [GetFollowableTopicsUseCase] property [getFollowableTopicsUseCase].
     *  This is a use case that returns a list of topics that can be followed.
     */
    @Before
    fun setup() {
        viewModel = ForYouViewModel(
            syncManager = syncManager,
            savedStateHandle = savedStateHandle,
            analyticsHelper = analyticsHelper,
            userDataRepository = userDataRepository,
            userNewsResourceRepository = userNewsResourceRepository,
            getFollowableTopics = getFollowableTopicsUseCase,
        )
    }

    /**
     * Test that the initial state of the [ForYouViewModel.onboardingUiState] property is
     * [OnboardingUiState.Loading] and the initial state of the [ForYouViewModel.feedState]
     * property is [NewsFeedUiState.Loading].
     */
    @Test
    fun stateIsInitiallyLoading(): TestResult = runTest {
        assertEquals(
            expected = OnboardingUiState.Loading,
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(expected = NewsFeedUiState.Loading, actual = viewModel.feedState.value)
    }

    /**
     * Test case to verify that the UI state remains in the loading state
     * when the followed topics are still loading.
     *
     * It launches two background coroutines to collect the `onboardingUiState` and `feedState` flows.
     * Then, it sends a list of sample topics to the `topicsRepository`. Finally, it asserts that
     * both [ForYouViewModel.onboardingUiState] and [ForYouViewModel.feedState] are still in their
     * respective loading states ([OnboardingUiState.Loading] and [NewsFeedUiState.Loading]).
     */
    @Test
    fun stateIsLoadingWhenFollowedTopicsAreLoading(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.onboardingUiState.collect()
        }
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedState.collect()
        }

        topicsRepository.sendTopics(topics = sampleTopics)

        assertEquals(
            expected = OnboardingUiState.Loading,
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(expected = NewsFeedUiState.Loading, actual = viewModel.feedState.value)
    }

    /**
     * Test case to verify that the UI state indicates loading when the app is syncing
     * and there are no followed interests.
     *
     * It sets the sync status to `true` using the `syncManager`.
     * Then, it launches a background coroutine to collect the `isSyncing` flow from the ViewModel.
     * Finally, it asserts that the `isSyncing` value in the ViewModel is `true`.
     */
    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoInterests(): TestResult = runTest {
        syncManager.setSyncing(true)

        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        assertEquals(
            expected = true,
            actual = viewModel.isSyncing.value,
        )
    }

    /**
     * Test case to verify that the onboarding UI state remains in the loading state
     * when the topics are still loading, even if the user has no followed topics.
     *
     * It launches two background coroutines to collect the `onboardingUiState` and `feedState` flows.
     * Then, it sets the followed topic IDs in the `userDataRepository` to an empty set.
     * Finally, it asserts that the `onboardingUiState` is still [OnboardingUiState.Loading]
     * and the `feedState` is [NewsFeedUiState.Success] with an empty feed.
     */
    @Test
    fun onboardingStateIsLoadingWhenTopicsAreLoading(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.onboardingUiState.collect() }
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.feedState.collect() }

        userDataRepository.setFollowedTopicIds(followedTopicIds = emptySet())

        assertEquals(
            expected = OnboardingUiState.Loading,
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(
            expected = NewsFeedUiState.Success(feed = emptyList()),
            actual = viewModel.feedState.value,
        )
    }

    /**
     * Test case to verify that the onboarding UI is shown when news resources are still loading.
     *
     * It launches two background coroutines to collect the `onboardingUiState` and `feedState` flows.
     * Then, it sends sample topics to the `topicsRepository` and sets an empty set of followed
     * topic IDs in the `userDataRepository`.
     * Finally, it asserts that the `onboardingUiState` is `OnboardingUiState.Shown` with the
     * sample topics, and the `feedState` is `NewsFeedUiState.Success` with an empty feed list.
     */
    @Test
    fun onboardingIsShownWhenNewsResourcesAreLoading(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.onboardingUiState.collect()
        }
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedState.collect()
        }

        topicsRepository.sendTopics(topics = sampleTopics)
        userDataRepository.setFollowedTopicIds(followedTopicIds = emptySet())

        assertEquals(
            expected = OnboardingUiState.Shown(
                topics = listOf(
                    FollowableTopic(
                        topic = Topic(
                            id = "0",
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "1",
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "2",
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
            ),
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(
            expected = NewsFeedUiState.Success(
                feed = emptyList(),
            ),
            actual = viewModel.feedState.value,
        )
    }

    /**
     * Test case to verify that the onboarding UI is shown after loading empty followed topics.
     *
     * It launches two background coroutines to collect the `onboardingUiState` and `feedState` flows.
     * Then, it sends sample topics to the `topicsRepository`, sets followed topic IDs to an empty set
     * in the `userDataRepository`, and sends sample news resources to the `newsRepository`.
     *
     * Finally, it asserts that the `onboardingUiState` is `OnboardingUiState.Shown` with the sample
     * topics (all marked as not followed) and the `feedState` is `NewsFeedUiState.Success` with an
     * empty feed.
     */
    @Test
    fun onboardingIsShownAfterLoadingEmptyFollowedTopics(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.onboardingUiState.collect()
        }
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedState.collect()
        }

        topicsRepository.sendTopics(topics = sampleTopics)
        userDataRepository.setFollowedTopicIds(followedTopicIds = emptySet())
        newsRepository.sendNewsResources(newsResources = sampleNewsResources)

        assertEquals(
            expected = OnboardingUiState.Shown(
                topics = listOf(
                    FollowableTopic(
                        topic = Topic(
                            id = "0",
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "1",
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "2",
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
            ),
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(
            expected = NewsFeedUiState.Success(
                feed = emptyList(),
            ),
            actual = viewModel.feedState.value,
        )
    }

    /**
     * Test case to verify that the onboarding UI is not shown after the user dismisses it.
     *
     * It launches two background coroutines to collect the `onboardingUiState` and `feedState` flows.
     * Then, it sends sample topics to the `topicsRepository`, sets some followed topic IDs in the
     * `userDataRepository`, and calls the [ForYouViewModel.dismissOnboarding] method of
     * [ForYouViewModel] property [viewModel].
     *
     * It asserts that the `onboardingUiState` is `OnboardingUiState.NotShown` and the `feedState`
     * is initially `NewsFeedUiState.Loading`.
     *
     * After sending sample news resources to the `newsRepository`, it asserts that the
     * `onboardingUiState` remains `OnboardingUiState.NotShown` and the `feedState` becomes
     * `NewsFeedUiState.Success` with the mapped user news resources.
     */
    @Test
    fun onboardingIsNotShownAfterUserDismissesOnboarding(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.onboardingUiState.collect()
        }
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedState.collect()
        }

        topicsRepository.sendTopics(topics = sampleTopics)

        val followedTopicIds: Set<String> = setOf("0", "1")
        val userData: UserData = emptyUserData.copy(followedTopics = followedTopicIds)
        userDataRepository.setUserData(userData = userData)
        viewModel.dismissOnboarding()

        assertEquals(
            expected = OnboardingUiState.NotShown,
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(expected = NewsFeedUiState.Loading, actual = viewModel.feedState.value)

        newsRepository.sendNewsResources(newsResources = sampleNewsResources)

        assertEquals(
            expected = OnboardingUiState.NotShown,
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(
            expected = NewsFeedUiState.Success(
                feed = sampleNewsResources.mapToUserNewsResources(userData = userData),
            ),
            actual = viewModel.feedState.value,
        )
    }

    /**
     * Test case to verify that the topic selection updates correctly after selecting a topic.
     *
     * It launches two background coroutines to collect the `onboardingUiState` and `feedState` flows.
     * Then, it sends sample topics to the `topicsRepository`, sets followed topic IDs to an empty
     * set in the `userDataRepository`, and sends sample news resources to the `newsRepository`.
     *
     * It asserts that the initial `onboardingUiState` is `OnboardingUiState.Shown` with all topics
     * marked as not followed, and the `feedState` is `NewsFeedUiState.Success` with an empty feed.
     *
     * Then, it simulates selecting a topic by calling `viewModel.updateTopicSelection()` with
     * `isChecked = true`.
     *
     * It asserts that the `onboardingUiState` is updated to reflect the selected topic, and the
     * `feedState` is updated to `NewsFeedUiState.Success` with the relevant news resources for
     * the selected topic.
     */
    @Test
    fun topicSelectionUpdatesAfterSelectingTopic(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.onboardingUiState.collect()
        }
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedState.collect()
        }

        topicsRepository.sendTopics(topics = sampleTopics)
        userDataRepository.setFollowedTopicIds(followedTopicIds = emptySet())
        newsRepository.sendNewsResources(newsResources = sampleNewsResources)

        assertEquals(
            expected = OnboardingUiState.Shown(
                topics = sampleTopics.map { topic: Topic ->
                    FollowableTopic(topic = topic, isFollowed = false)
                },
            ),
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(
            expected = NewsFeedUiState.Success(
                feed = emptyList(),
            ),
            actual = viewModel.feedState.value,
        )

        val followedTopicId: String = sampleTopics[1].id
        viewModel.updateTopicSelection(topicId = followedTopicId, isChecked = true)

        assertEquals(
            expected = OnboardingUiState.Shown(
                topics = sampleTopics.map { topic: Topic ->
                    FollowableTopic(topic = topic, isFollowed = topic.id == followedTopicId)
                },
            ),
            actual = viewModel.onboardingUiState.value,
        )

        val userData: UserData = emptyUserData.copy(followedTopics = setOf(followedTopicId))

        assertEquals(
            expected = NewsFeedUiState.Success(
                feed = listOf(
                    UserNewsResource(newsResource = sampleNewsResources[1], userData = userData),
                    UserNewsResource(newsResource = sampleNewsResources[2], userData = userData),
                ),
            ),
            actual = viewModel.feedState.value,
        )
    }

    /**
     * Test case to verify that the topic selection updates correctly after unselecting a topic.
     *
     * It launches two background coroutines to collect the `onboardingUiState` and `feedState` flows.
     * Then, it sends sample topics to the `topicsRepository`, sets followed topic IDs to an empty set
     * in the `userDataRepository`, and sends sample news resources to the `newsRepository`.
     *
     * It simulates selecting and then unselecting a topic by calling `updateTopicSelection` twice.
     *
     * Finally, it asserts that:
     * - The `onboardingUiState` is `OnboardingUiState.Shown` with all topics marked as not followed.
     * - The `feedState` is `NewsFeedUiState.Success` with an empty feed.
     */
    @Test
    fun topicSelectionUpdatesAfterUnselectingTopic(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.onboardingUiState.collect()
        }
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedState.collect()
        }

        topicsRepository.sendTopics(topics = sampleTopics)
        userDataRepository.setFollowedTopicIds(followedTopicIds = emptySet())
        newsRepository.sendNewsResources(newsResources = sampleNewsResources)
        viewModel.updateTopicSelection(topicId = "1", isChecked = true)
        viewModel.updateTopicSelection(topicId = "1", isChecked = false)

        advanceUntilIdle()
        assertEquals(
            expected = OnboardingUiState.Shown(
                topics = listOf(
                    FollowableTopic(
                        topic = Topic(
                            id = "0",
                            name = "Headlines",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "1",
                            name = "UI",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                    FollowableTopic(
                        topic = Topic(
                            id = "2",
                            name = "Tools",
                            shortDescription = "",
                            longDescription = "long description",
                            url = "URL",
                            imageUrl = "image URL",
                        ),
                        isFollowed = false,
                    ),
                ),
            ),
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(
            expected = NewsFeedUiState.Success(
                feed = emptyList(),
            ),
            actual = viewModel.feedState.value,
        )
    }

    /**
     * Test case to verify that the news resource selection is updated correctly
     * after loading the followed topics.
     *
     * It launches two background coroutines to collect the `onboardingUiState` and `feedState` flows.
     * Then, it sets the initial user data with some followed topics and hides onboarding.
     * It sends sample topics to `topicsRepository`, user data to `userDataRepository`, and
     * news resources to `newsRepository`.
     *
     * Next, it updates the saved status of a specific news resource.
     *
     * Finally, it asserts that the `onboardingUiState` is `OnboardingUiState.NotShown` and the
     * `feedState` is `NewsFeedUiState.Success` with the expected list of user news resources,
     * including the updated bookmarked status.
     */
    @Test
    fun newsResourceSelectionUpdatesAfterLoadingFollowedTopics(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.onboardingUiState.collect()
        }
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.feedState.collect()
        }

        val followedTopicIds: Set<String> = setOf("1")
        val userData: UserData = emptyUserData.copy(
            followedTopics = followedTopicIds,
            shouldHideOnboarding = true,
        )

        topicsRepository.sendTopics(topics = sampleTopics)
        userDataRepository.setUserData(userData = userData)
        newsRepository.sendNewsResources(newsResources = sampleNewsResources)

        val bookmarkedNewsResourceId = "2"
        viewModel.updateNewsResourceSaved(
            newsResourceId = bookmarkedNewsResourceId,
            isChecked = true,
        )

        val userDataExpected: UserData = userData.copy(
            bookmarkedNewsResources = setOf(bookmarkedNewsResourceId),
        )

        assertEquals(
            expected = OnboardingUiState.NotShown,
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(
            expected = NewsFeedUiState.Success(
                feed = listOf(
                    UserNewsResource(
                        newsResource = sampleNewsResources[1],
                        userData = userDataExpected,
                    ),
                    UserNewsResource(
                        newsResource = sampleNewsResources[2],
                        userData = userDataExpected,
                    ),
                ),
            ),
            actual = viewModel.feedState.value,
        )
    }

    /**
     * Test case to verify that a deep-linked news resource is fetched and then reset after viewing.
     *
     * It launches a background coroutine to collect the `deepLinkedNewsResource` flow from the
     * ViewModel. Then, it sends sample news resources to the `newsRepository`, sets empty user
     * data in the `userDataRepository`, and sets a deep link news resource ID in the
     * `savedStateHandle`.
     *
     * It asserts that the `deepLinkedNewsResource` in the ViewModel matches the expected
     * `UserNewsResource`.
     *
     * After calling `viewModel.onDeepLinkOpened()` with the news resource ID, it asserts that the
     * `deepLinkedNewsResource` becomes null.
     *
     * Finally, it verifies that an analytics event for "news_deep_link_opened" with the correct
     * news resource ID has been logged.
     */
    @Test
    fun deepLinkedNewsResourceIsFetchedAndResetAfterViewing(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.deepLinkedNewsResource.collect()
        }

        newsRepository.sendNewsResources(newsResources = sampleNewsResources)
        userDataRepository.setUserData(userData = emptyUserData)
        savedStateHandle[DEEP_LINK_NEWS_RESOURCE_ID_KEY] = sampleNewsResources.first().id

        assertEquals(
            expected = UserNewsResource(
                newsResource = sampleNewsResources.first(),
                userData = emptyUserData,
            ),
            actual = viewModel.deepLinkedNewsResource.value,
        )

        viewModel.onDeepLinkOpened(
            newsResourceId = sampleNewsResources.first().id,
        )

        assertNull(
            actual = viewModel.deepLinkedNewsResource.value,
        )

        assertTrue(
            actual = analyticsHelper.hasLogged(
                event = AnalyticsEvent(
                    type = "news_deep_link_opened",
                    extras = listOf(
                        Param(
                            key = DEEP_LINK_NEWS_RESOURCE_ID_KEY,
                            value = sampleNewsResources.first().id,
                        ),
                    ),
                ),
            ),
        )
    }

    /**
     * Test case to verify that the bookmark state is updated correctly when
     * [ForYouViewModel.updateNewsResourceSaved] is called.
     *
     * It calls `viewModel.updateNewsResourceSaved` twice:
     * 1. With `isChecked = true` to bookmark a news resource.
     * 2. With `isChecked = false` to unbookmark the same news resource.
     *
     * It asserts that the `bookmarkedNewsResources` in the `userDataRepository` is updated
     * accordingly after each call.
     */
    @Test
    fun whenUpdateNewsResourceSavedIsCalled_bookmarkStateIsUpdated(): TestResult = runTest {
        val newsResourceId = "123"
        viewModel.updateNewsResourceSaved(newsResourceId = newsResourceId, isChecked = true)

        assertEquals(
            expected = setOf(newsResourceId),
            actual = userDataRepository.userData.first().bookmarkedNewsResources,
        )

        viewModel.updateNewsResourceSaved(newsResourceId = newsResourceId, isChecked = false)

        assertEquals(
            expected = emptySet(),
            actual = userDataRepository.userData.first().bookmarkedNewsResources,
        )
    }
}

/**
 * A sample list of [Topic] instances used for testing.
 * This list contains three topics: "Headlines", "UI", and "Tools".
 * Each topic has a unique ID, name, short description, long description, URL, and image URL.
 */
private val sampleTopics = listOf(
    Topic(
        id = "0",
        name = "Headlines",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Topic(
        id = "1",
        name = "UI",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
    Topic(
        id = "2",
        name = "Tools",
        shortDescription = "",
        longDescription = "long description",
        url = "URL",
        imageUrl = "image URL",
    ),
)

/**
 * A sample list of [NewsResource] instances used for testing.
 * This list contains three news resources, each with a unique ID, title, content, URL,
 * header image URL, publish date, type, and associated topics.
 */
private val sampleNewsResources = listOf(
    NewsResource(
        id = "1",
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        content = "Thank you everyone for following the Now in Android series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        url = "https://youtu.be/-fJ6poHQrjM",
        headerImageUrl = "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(
            Topic(
                id = "0",
                name = "Headlines",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
    NewsResource(
        id = "2",
        title = "Transformations and customisations in the Paging Library",
        content = "A demonstration of different operations that can be performed with Paging. " +
            "Transformations like inserting separators, when to create a new pager, and " +
            "customisation options for consuming PagingData.",
        url = "https://youtu.be/ZARz0pjm5YM",
        headerImageUrl = "https://i.ytimg.com/vi/ZARz0pjm5YM/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-01T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(
            Topic(
                id = "1",
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
    NewsResource(
        id = "3",
        title = "Community tip on Paging",
        content = "Tips for using the Paging library from the developer community",
        url = "https://youtu.be/r5JgIyS3t3s",
        headerImageUrl = "https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-08T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(
            Topic(
                id = "1",
                name = "UI",
                shortDescription = "",
                longDescription = "long description",
                url = "URL",
                imageUrl = "image URL",
            ),
        ),
    ),
)
