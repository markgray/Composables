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

package com.google.samples.apps.nowinandroid.feature.topic

import com.google.samples.apps.nowinandroid.core.data.repository.CompositeUserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.testing.repository.TestNewsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestTopicsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Unit tests for [TopicViewModel].
 *
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 */
class TopicViewModelTest {

    /**
     * Overrides the Dispatchers.Main used in Coroutines by the Main dispatcher rule.
     *
     * We need to override the Dispatcher.Main because the view model classes use viewModelScope
     * which uses the Dispatcher.Main.
     */
    @get:Rule
    val dispatcherRule: MainDispatcherRule = MainDispatcherRule()

    /**
     * A test implementation of [UserDataRepository] that allows setting the followed topic IDs.
     */
    private val userDataRepository = TestUserDataRepository()

    /**
     * A test implementation of [TopicsRepository] that allows controlling the topics it emits.
     */
    private val topicsRepository = TestTopicsRepository()

    /**
     * A test implementation of [NewsRepository] that allows controlling the news resources backing data.
     */
    private val newsRepository = TestNewsRepository()

    /**
     * A test implementation of [UserNewsResourceRepository] that combines the behavior of the
     * [newsRepository] and [userDataRepository].
     */
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )

    /**
     * The [TopicViewModel] under test, initialized in our [setup] function before each test.
     */
    private lateinit var viewModel: TopicViewModel

    /**
     * Sets up the test by initializing the [viewModel] with test data. The @[Before] annotation
     * ensures that this function is executed before each test. We initialize our [TopicViewModel]
     * property [viewModel] to an instance or [TopicViewModel] with the arguments:
     *  - `userDataRepository`: is our [TestUserDataRepository] property [userDataRepository].
     *  - `topicsRepository`: is our [TestTopicsRepository] property [topicsRepository].
     *  - `userNewsResourceRepository`: is our [CompositeUserNewsResourceRepository] property
     *  [userNewsResourceRepository].
     *  - `topicId`: is the [Topic.id] of the first [FollowableTopic] in our [testInputTopics] list.
     */
    @Before
    fun setup() {
        viewModel = TopicViewModel(
            userDataRepository = userDataRepository,
            topicsRepository = topicsRepository,
            userNewsResourceRepository = userNewsResourceRepository,
            topicId = testInputTopics[0].topic.id,
        )
    }

    /**
     * Checks that the [TopicViewModel.topicId] is correctly exposed and matches the initial
     * value passed in the constructor.
     */
    @Test
    fun topicId_matchesTopicIdFromSavedStateHandle(): Unit =
        assertEquals(expected = testInputTopics[0].topic.id, actual = viewModel.topicId)

    /**
     * This test checks that when the [TopicViewModel.topicUiState] is in a `Success` state, the
     * topic it holds is the same as the topic with the same ID in the [topicsRepository]. It does
     * this by:
     * 1. Launching a coroutine that collects the [TopicViewModel.topicUiState] `StateFlow`.
     * 2. Having our [TestTopicsRepository.sendTopics] method emit the [Topic] objects that are
     * contained in our [testInputTopics] list.
     * 3. Having our [TestUserDataRepository.setFollowedTopicIds] method emit a [Set] of the
     * [Topic.id] of the second entry in our [testInputTopics] list (this makes the first entry
     * in our [testInputTopics] list to be considered unfollowed, and the second entry to be
     * considered followed).
     * 4. It then retrieves the current value of the [TopicViewModel.topicUiState] `StateFlow` into
     * its `val item` variable and asserts that this is a [TopicUiState.Success].
     * 5. It then retrieves the [Topic] with the same [Topic.id] as the first entry in our
     * [testInputTopics] list from our [TestTopicsRepository] into its `val topicFromRepository`
     * variable.
     * 6. Finally it asserts that this `topicFromRepository` is equal to the [FollowableTopic.topic]
     * property of the [TopicUiState.Success.followableTopic] property of `item`.
     */
    @Test
    fun uiStateTopic_whenSuccess_matchesTopicFromRepository(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.topicUiState.collect() }

        topicsRepository.sendTopics(topics = testInputTopics.map(transform = FollowableTopic::topic))
        userDataRepository.setFollowedTopicIds(followedTopicIds = setOf(testInputTopics[1].topic.id))
        val item: TopicUiState = viewModel.topicUiState.value
        assertIs<TopicUiState.Success>(value = item)

        val topicFromRepository: Topic = topicsRepository.getTopic(
            id = testInputTopics[0].topic.id,
        ).first()

        assertEquals(expected = topicFromRepository, actual = item.followableTopic.topic)
    }

    /**
     * Test case to verify that the initial state of [TopicViewModel.newsUiState] is
     * [NewsUiState.Loading]. This test ensures that when the ViewModel is initialized,
     * it correctly reports a loading state for news data before any data is actually
     * fetched or processed.
     *
     * The test is performed by:
     *  1. Accessing the current value of `viewModel.newsUiState`.
     *  2. Asserting that this value is equal to `NewsUiState.Loading`.
     *
     * This test uses `runTest` to execute in an [EmptyCoroutineContext] coroutine context suitable
     * for testing suspend functions and Flows.
     */
    @Test
    fun uiStateNews_whenInitialized_thenShowLoading(): TestResult = runTest {
        assertEquals(expected = NewsUiState.Loading, actual = viewModel.newsUiState.value)
    }

    /**
     * This test checks that when the [TopicViewModel] is initialized, its
     * [TopicViewModel.topicUiState] property is [TopicUiState.Loading].
     * It just asserts that the current value of the [TopicViewModel.topicUiState]
     * `StateFlow` is equal to [TopicUiState.Loading].
     */
    @Test
    fun uiStateTopic_whenInitialized_thenShowLoading(): TestResult = runTest {
        assertEquals(expected = TopicUiState.Loading, actual = viewModel.topicUiState.value)
    }

    /**
     * This test checks that when the user's followed topic IDs are successfully loaded but the
     * specific topic data is still loading, the [TopicViewModel.topicUiState] remains in the
     * `Loading` state.
     *
     * It performs the following steps:
     *  1. Launches a coroutine that collects the [TopicViewModel.topicUiState] `StateFlow`. This is
     *  necessary because `topicUiState` is a `stateIn` Flow, which only starts emitting when
     *  there's a collector.
     *  2. Simulates a successful load of followed topic IDs by calling
     *  [TestUserDataRepository.setFollowedTopicIds]. It passes a set containing the ID of the
     *  second topic in `testInputTopics`.
     *  3. Asserts that the current value of [TopicViewModel.topicUiState] is [TopicUiState.Loading].
     *  This verifies that even though the followed IDs are available, the UI state for the
     *  topic itself doesn't transition to `Success` until the topic data is also loaded.
     */
    @Test
    fun uiStateTopic_whenFollowedIdsSuccessAndTopicLoading_thenShowLoading(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.topicUiState.collect()
        }

        userDataRepository.setFollowedTopicIds(followedTopicIds = setOf(testInputTopics[1].topic.id))
        assertEquals(expected = TopicUiState.Loading, actual = viewModel.topicUiState.value)
    }

    /**
     * This test checks that when the user's followed topic IDs have been successfully retrieved and
     * the topics themselves have been successfully retrieved, then [TopicViewModel.topicUiState]
     * is [TopicUiState.Success] and [TopicViewModel.newsUiState] is [NewsUiState.Loading].
     *
     * It does this by:
     *  1. Launching a coroutine that collects the [TopicViewModel.topicUiState] `StateFlow`.
     *  2. Having our [TestTopicsRepository.sendTopics] method emit the [Topic] objects that are
     *  contained in our [testInputTopics] list.
     *  3. Having our [TestUserDataRepository.setFollowedTopicIds] method emit a [Set] of the
     *  [Topic.id] of the second entry in our [testInputTopics] list (this makes the first entry
     *  in our [testInputTopics] list to be considered unfollowed, and the second entry to be
     *  considered followed).
     *  4. It then retrieves the current value of the [TopicViewModel.topicUiState] `StateFlow` into
     *  its `val topicUiState` variable and the current value of the [TopicViewModel.newsUiState]
     *  `StateFlow` into its `val newsUiState` variable.
     *  5. Finally it asserts that `topicUiState` is a [TopicUiState.Success] and `newsUiState` is
     *  a [NewsUiState.Loading].
     */
    @Test
    fun uiStateTopic_whenFollowedIdsSuccessAndTopicSuccess_thenTopicSuccessAndNewsLoading(): TestResult =
        runTest {
            backgroundScope.launch(context = UnconfinedTestDispatcher()) {
                viewModel.topicUiState.collect()
            }

            topicsRepository.sendTopics(topics = testInputTopics.map { it.topic })
            userDataRepository.setFollowedTopicIds(followedTopicIds = setOf(testInputTopics[1].topic.id))
            val topicUiState: TopicUiState = viewModel.topicUiState.value
            val newsUiState: NewsUiState = viewModel.newsUiState.value

            assertIs<TopicUiState.Success>(value = topicUiState)
            assertIs<NewsUiState.Loading>(value = newsUiState)
        }

    /**
     * This test checks that when the user's followed topic IDs have been successfully retrieved,
     * the topics themselves have been successfully retrieved, and the news resources have been
     * successfully retrieved, then both [TopicViewModel.topicUiState] and
     * [TopicViewModel.newsUiState] are in a `Success` state.
     *
     * It does this by:
     *  1. Launching a coroutine that collects a [Pair] of [TopicViewModel.topicUiState] and
     *  [TopicViewModel.newsUiState] `StateFlow`'s. This ensures that both flows are active.
     *  2. Having our [TestTopicsRepository.sendTopics] method emit the [Topic] objects that are
     *  contained in our [testInputTopics] list.
     *  3. Having our [TestUserDataRepository.setFollowedTopicIds] method emit a [Set] of the
     *  [Topic.id]  of the second entry in our [testInputTopics] list (this makes the first entry
     *  in our [testInputTopics] list to be considered unfollowed, and the second entry to be
     *  considered followed).
     *  4. Having our [TestNewsRepository.sendNewsResources] method emit our [sampleNewsResources]
     *  list of [NewsResource].
     *  5. It then retrieves the current value of the [TopicViewModel.topicUiState] `StateFlow` into
     *  its `val topicUiState` variable and the current value of the [TopicViewModel.newsUiState]
     *  `StateFlow` into its `val newsUiState` variable.
     *  6. Finally it asserts that `topicUiState` is a [TopicUiState.Success] and `newsUiState` is
     *  a [NewsUiState.Success].
     */
    @Test
    fun uiStateTopic_whenFollowedIdsSuccessAndTopicSuccessAndNewsIsSuccess_thenAllSuccess(): TestResult =
        runTest {
            backgroundScope.launch(context = UnconfinedTestDispatcher()) {
                combine(
                    flow = viewModel.topicUiState,
                    flow2 = viewModel.newsUiState,
                    transform = ::Pair,
                ).collect()
            }
            topicsRepository.sendTopics(topics = testInputTopics.map { it.topic })
            userDataRepository.setFollowedTopicIds(followedTopicIds = setOf(testInputTopics[1].topic.id))
            newsRepository.sendNewsResources(newsResources = sampleNewsResources)
            val topicUiState: TopicUiState = viewModel.topicUiState.value
            val newsUiState: NewsUiState = viewModel.newsUiState.value

            assertIs<TopicUiState.Success>(value = topicUiState)
            assertIs<NewsUiState.Success>(value = newsUiState)
        }

    /**
     * This test checks that when the user toggles the follow state of the current topic (the topic
     * whose [Topic.id] matches the `topicId` that the [viewModel] was constructed with) its
     * [TopicViewModel.topicUiState] is updated to reflect that change.
     *
     * It does this by:
     *  1. Launching a coroutine that collects the [TopicViewModel.topicUiState] `StateFlow`. This
     *  is necessary because `topicUiState` is a `stateIn` Flow, which only starts emitting when
     *  there's a collector.
     *  2. Having our [TestTopicsRepository.sendTopics] method emit the [Topic] objects that are
     *  contained in our [testInputTopics] list.
     *  3. Having our [TestUserDataRepository.setFollowedTopicIds] method emit a [Set] of the
     *  [Topic.id] of the second entry in our [testInputTopics] list (this makes the first entry
     *  in our [testInputTopics] list to be considered unfollowed, and the second entry to be
     *  considered followed).
     *  4. It then calls the [TopicViewModel.followTopicToggle] method with `followed` = `true`
     *  (this call should update the `isFollowed` property of the current topic to `true` causing
     *  the [TopicViewModel.topicUiState] to be updated to a [TopicUiState.Success] whose
     *  [FollowableTopic] is the first entry in our [testOutputTopics] list.
     *  5. Finally it asserts that the current value of the [TopicViewModel.topicUiState] `StateFlow`
     *  is equal to the [TopicUiState.Success] whose [FollowableTopic] is the first entry in our
     *  [testOutputTopics] list.
     */
    @Test
    fun uiStateTopic_whenFollowingTopic_thenShowUpdatedTopic(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) {
            viewModel.topicUiState.collect()
        }

        topicsRepository.sendTopics(topics = testInputTopics.map { it.topic })
        // Set which topic IDs are followed, not including 0.
        userDataRepository.setFollowedTopicIds(followedTopicIds = setOf(testInputTopics[1].topic.id))

        viewModel.followTopicToggle(followed = true)

        assertEquals(
            expected = TopicUiState.Success(followableTopic = testOutputTopics[0]),
            actual = viewModel.topicUiState.value,
        )
    }
}

/**
 * Name of the first test topic.
 */
private const val TOPIC_1_NAME = "Android Studio"

/**
 * Name of the second test topic.
 */
private const val TOPIC_2_NAME = "Build"

/**
 * Name of the third test topic.
 */
private const val TOPIC_3_NAME = "Compose"

/**
 * Short description of the test topic.
 */
private const val TOPIC_SHORT_DESC = "At vero eos et accusamus."

/**
 * Long description of the test topic.
 */
private const val TOPIC_LONG_DESC = "At vero eos et accusamus et iusto odio dignissimos ducimus."

/**
 * URL of the test topic.
 */
private const val TOPIC_URL = "URL"

/**
 * Image URL of the test topic.
 */
private const val TOPIC_IMAGE_URL = "Image URL"

/**
 * A list of [FollowableTopic] that serves as the input data for the [TestTopicsRepository] when it
 * is being used to test our [TopicViewModel]. The `isFollowed` property of the first [FollowableTopic]
 * is `true`, while the `isFollowed` property of the second and third [FollowableTopic] is `false`.
 */
private val testInputTopics: List<FollowableTopic> = listOf(
    FollowableTopic(
        Topic(
            id = "0",
            name = TOPIC_1_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = true,
    ),
    FollowableTopic(
        Topic(
            id = "1",
            name = TOPIC_2_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = false,
    ),
    FollowableTopic(
        Topic(
            id = "2",
            name = TOPIC_3_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = false,
    ),
)

/**
 * A list of [FollowableTopic] that serves as the expected output data when testing the
 * [TopicViewModel.topicUiState] after a user action, such as toggling the follow state of a topic.
 * The `isFollowed` property of the first and second [FollowableTopic] is `true`, while the
 * `isFollowed` property of the third [FollowableTopic] is `false`.
 */
private val testOutputTopics: List<FollowableTopic> = listOf(
    FollowableTopic(
        Topic(
            id = "0",
            name = TOPIC_1_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = true,
    ),
    FollowableTopic(
        Topic(
            id = "1",
            name = TOPIC_2_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = true,
    ),
    FollowableTopic(
        Topic(
            id = "2",
            name = TOPIC_3_NAME,
            shortDescription = TOPIC_SHORT_DESC,
            longDescription = TOPIC_LONG_DESC,
            url = TOPIC_URL,
            imageUrl = TOPIC_IMAGE_URL,
        ),
        isFollowed = false,
    ),
)

/**
 * A sample list of [NewsResource] that is used to feed the [TestNewsRepository] when testing the
 * [TopicViewModel.newsUiState] property. It consists of a single [NewsResource] object.
 */
private val sampleNewsResources: List<NewsResource> = listOf(
    NewsResource(
        id = "1",
        title = "Thanks for helping us reach 1M YouTube Subscribers",
        content = "Thank you everyone for following the Now in Android series and everything the " +
            "Android Developers YouTube channel has to offer. During the Android Developer " +
            "Summit, our YouTube channel reached 1 million subscribers! Here’s a small video to " +
            "thank you all.",
        url = "https://youtu.be/-fJ6poHQrjM",
        headerImageUrl = "https://i.ytimg.com/vi/-fJ6poHQrjM/maxresdefault.jpg",
        publishDate = Instant.parse(input = "2021-11-09T00:00:00.000Z"),
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
)
