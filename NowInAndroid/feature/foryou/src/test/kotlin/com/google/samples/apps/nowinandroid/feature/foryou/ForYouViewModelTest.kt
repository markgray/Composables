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
import kotlinx.coroutines.Dispatchers
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
     * A test version of [AnalyticsHelper] that records events in a list, makes them available
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
     * This function is annotated with `@Before` to ensure it runs before each test method.
     * TODO: Continue here.
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

    @Test
    fun stateIsInitiallyLoading(): TestResult = runTest {
        assertEquals(
            expected = OnboardingUiState.Loading,
            actual = viewModel.onboardingUiState.value,
        )
        assertEquals(expected = NewsFeedUiState.Loading, actual = viewModel.feedState.value)
    }

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

    @Test
    fun stateIsLoadingWhenAppIsSyncingWithNoInterests(): TestResult = runTest {
        syncManager.setSyncing(true)

        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.isSyncing.collect() }

        assertEquals(
            expected = true,
            actual = viewModel.isSyncing.value,
        )
    }

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
            actual = viewModel.feedState.value
        )
    }

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
                        userData = userDataExpected
                    ),
                    UserNewsResource(
                        newsResource = sampleNewsResources[2],
                        userData = userDataExpected
                    ),
                ),
            ),
            actual = viewModel.feedState.value,
        )
    }

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
