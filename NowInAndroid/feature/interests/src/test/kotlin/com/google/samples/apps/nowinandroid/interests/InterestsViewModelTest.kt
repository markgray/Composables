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

package com.google.samples.apps.nowinandroid.interests

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.domain.GetFollowableTopicsUseCase
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.testing.repository.TestTopicsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import com.google.samples.apps.nowinandroid.feature.interests.InterestsUiState
import com.google.samples.apps.nowinandroid.feature.interests.InterestsViewModel
import com.google.samples.apps.nowinandroid.feature.interests.navigation.InterestsRoute
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

/**
 * Unit tests for [InterestsViewModel]. The @[RunWith] annotation causes the tests to be run with
 * the test runner [RobolectricTestRunner], which is a test runner that allows the use of
 * Android framework classes like [SavedStateHandle].
 *
 * To learn more about how this test handles Flows created with stateIn, see
 * https://developer.android.com/kotlin/flow/test#statein
 *
 * These tests use Robolectric because the subject under test (the ViewModel) uses
 * `SavedStateHandle.toRoute` which has a dependency on `android.os.Bundle`.
 *
 * TODO: Remove Robolectric if/when AndroidX Navigation API is updated to remove Android dependency.
 *  See https://issuetracker.google.com/340966212.
 */
@RunWith(RobolectricTestRunner::class)
class InterestsViewModelTest {

    /**
     * Overrides the main dispatcher for testing, ensuring that suspend functions run on the
     * test thread. This is a JUnit Test Rule. It's crucial for testing code that uses Kotlin
     * Coroutines, especially those involving Dispatchers.Main. It replaces the main dispatcher
     * with a TestDispatcher during the test, allowing you to control the execution of coroutines
     * on the main thread and making tests more predictable and reliable.
     */
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    /**
     * A test implementation of the [UserDataRepository] that allows for setting the followed
     * topics. This is used to simulate different states of the user's followed topics and verify
     * that the [InterestsViewModel] behaves correctly.
     */
    private val userDataRepository = TestUserDataRepository()

    /**
     * A test implementation of the [TopicsRepository] interface, used to simulate the behavior of
     * a real topics repository in tests.
     */
    private val topicsRepository = TestTopicsRepository()

    /**
     * The use case used to retrieve the list of followable topics. It is initialized with the
     * test repositories [topicsRepository] and [userDataRepository].
     */
    private val getFollowableTopicsUseCase = GetFollowableTopicsUseCase(
        topicsRepository = topicsRepository,
        userDataRepository = userDataRepository,
    )

    /**
     * The ViewModel under test. This is initialized in the [setup] function.
     */
    private lateinit var viewModel: InterestsViewModel

    /**
     * Sets up the test environment before each test.
     * This function initializes the [viewModel] with a [SavedStateHandle] containing the
     * initial topic ID from [testInputTopics], our [TestUserDataRepository] property
     * [userDataRepository], and our [getFollowableTopicsUseCase] property.
     */
    @Before
    fun setup() {
        viewModel = InterestsViewModel(
            savedStateHandle = SavedStateHandle(
                route = InterestsRoute(initialTopicId = testInputTopics[0].topic.id),
            ),
            userDataRepository = userDataRepository,
            getFollowableTopics = getFollowableTopicsUseCase,
        )
    }

    /**
     * Tests that the initial UI state is [InterestsUiState.Loading].
     * This is because the ViewModel starts in a loading state while it fetches the initial data.
     */
    @Test
    fun uiState_whenInitialized_thenShowLoading(): TestResult = runTest {
        assertEquals(expected = InterestsUiState.Loading, actual = viewModel.uiState.value)
    }

    /**
     * Tests that the UI state is [InterestsUiState.Loading] when the followed topics are
     * still loading. This is achieved by launching a coroutine that collects the uiState Flow
     * and then setting the followed topics to an empty set. This simulates the scenario where the
     * ViewModel is initialized but the followed topics have not yet been loaded.
     */
    @Test
    fun uiState_whenFollowedTopicsAreLoading_thenShowLoading(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        userDataRepository.setFollowedTopicIds(followedTopicIds = emptySet())
        assertEquals(expected = InterestsUiState.Loading, actual = viewModel.uiState.value)
    }

    /**
     * Tests that the UI state is updated correctly when a new topic is followed.
     * This is achieved by:
     *  1. Launching a coroutine that collects the uiState Flow.
     *  2. Setting the initial followed topics to a set containing only the first topic.
     *  3. Asserting that the second topic is not followed.
     *  4. Calling the `followTopic` function to follow the second topic.
     *  5. Asserting that the UI state is updated to reflect that the second topic is now followed.
     */
    @Test
    fun uiState_whenFollowingNewTopic_thenShowUpdatedTopics(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val toggleTopicId: String = testOutputTopics[1].topic.id
        topicsRepository.sendTopics(topics = testInputTopics.map { it.topic })
        userDataRepository.setFollowedTopicIds(followedTopicIds = setOf(testInputTopics[0].topic.id))

        assertEquals(
            expected = false,
            actual = (viewModel.uiState.value as InterestsUiState.Interests)
                .topics.first { it.topic.id == toggleTopicId }.isFollowed,
        )

        viewModel.followTopic(
            followedTopicId = toggleTopicId,
            followed = true,
        )

        assertEquals(
            expected = InterestsUiState.Interests(
                topics = testOutputTopics,
                selectedTopicId = testInputTopics[0].topic.id,
            ),
            actual = viewModel.uiState.value,
        )
    }

    /**
     * Tests that the UI state is updated correctly when a topic is unfollowed.
     * This is achieved by:
     *  1. Launching a coroutine that collects the uiState Flow.
     *  2. Setting the initial followed topics to a set containing the first two topics.
     *  3. Asserting that the second topic is followed.
     *  4. Calling the `followTopic` function to unfollow the second topic.
     *  5. Asserting that the UI state is updated to reflect that the second topic is no longer
     *  followed.
     */
    @Test
    fun uiState_whenUnfollowingTopics_thenShowUpdatedTopics(): TestResult = runTest {
        backgroundScope.launch(context = UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val toggleTopicId: String = testOutputTopics[1].topic.id

        topicsRepository.sendTopics(topics = testOutputTopics.map { it.topic })
        userDataRepository.setFollowedTopicIds(
            followedTopicIds = setOf(testOutputTopics[0].topic.id, testOutputTopics[1].topic.id),
        )

        assertEquals(
            expected = true,
            actual = (viewModel.uiState.value as InterestsUiState.Interests)
                .topics.first { it.topic.id == toggleTopicId }.isFollowed,
        )

        viewModel.followTopic(
            followedTopicId = toggleTopicId,
            followed = false,
        )

        assertEquals(
            expected = InterestsUiState.Interests(
                topics = testInputTopics,
                selectedTopicId = testInputTopics[0].topic.id,
            ),
            actual = viewModel.uiState.value,
        )
    }
}

/**
 * Constant for the name of the first topic.
 */
private const val TOPIC_1_NAME = "Android Studio"

/**
 * Constant for the name of the second topic.
 */
private const val TOPIC_2_NAME = "Build"

/**
 * Constant for the name of the third topic.
 */
private const val TOPIC_3_NAME = "Compose"

/**
 * Constant for the short description of a topic.
 */
private const val TOPIC_SHORT_DESC = "At vero eos et accusamus."

/**
 * Constant for the long description of a topic.
 */
private const val TOPIC_LONG_DESC = "At vero eos et accusamus et iusto odio dignissimos ducimus."

/**
 * Constant for the URL of a topic.
 */
private const val TOPIC_URL = "URL"

/**
 * Constant for the URL of a topic's image.
 */
private const val TOPIC_IMAGE_URL = "Image URL"

/**
 * A list of [FollowableTopic]s that are used as input for testing.
 * This list represents the initial state of the topics before any user interaction.
 *
 * It contains three topics:
 *  - The first topic is followed.
 *  - The second and third topics are not followed.
 */
private val testInputTopics = listOf(
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
 * A list of [FollowableTopic]s that represents the expected output after a follow operation.
 * The first two topics are followed, and the third is not. This is used to verify that the
 * ViewModel correctly updates the UI state after a follow operation.
 */
private val testOutputTopics = listOf(
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
