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

package com.google.samples.apps.nowinandroid.core.domain

import com.google.samples.apps.nowinandroid.core.data.repository.TopicsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.domain.TopicSortField.NAME
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.testing.repository.TestTopicsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [GetFollowableTopicsUseCase]. The main goal of this test class is to verify that
 * the [GetFollowableTopicsUseCase] correctly retrieves and processes topic data, including their
 * "followed" status and sorting, under different conditions.
 */
class GetFollowableTopicsUseCaseTest {

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
     * A test implementation of the [TopicsRepository] that allows for emitting test data.
     * This is used to simulate different scenarios for topic data, such as an empty list, a list
     * with specific topics, or error conditions.
     */
    private val topicsRepository = TestTopicsRepository()

    /**
     * A test implementation of the [UserDataRepository] that allows for emitting test data.
     * This is used to simulate different scenarios for user data, such as an empty list of
     * followed topics, a list with specific followed topics, or error conditions.
     */
    private val userDataRepository = TestUserDataRepository()

    /**
     * The use case under test. It's initialized with test repositories to control the data
     * flow and simulate various scenarios. This allows for focused testing of the use case's
     * logic, independent of the actual data sources.
     */
    val useCase: GetFollowableTopicsUseCase = GetFollowableTopicsUseCase(
        topicsRepository,
        userDataRepository,
    )

    /**
     * This test verifies that when the use case is invoked without any sorting parameters, it
     * returns a list of [FollowableTopic]s in their original order, correctly reflecting their
     * "followed" status. It simulates a scenario where some topics are followed and others are not,
     * and asserts that the output matches the expected list of followable topics with their
     * respective "followed" states.
     */
    @Test
    fun whenNoParams_followableTopicsAreReturnedWithNoSorting(): TestResult = runTest {
        // Obtain a stream of followable topics.
        val followableTopics: Flow<List<FollowableTopic>> = useCase()

        // Send some test topics and their followed state.
        topicsRepository.sendTopics(topics = testTopics)
        userDataRepository.setFollowedTopicIds(
            followedTopicIds = setOf(
                testTopics[0].id,
                testTopics[2].id,
            ),
        )

        // Check that the order hasn't changed and that the correct topics are marked as followed.
        assertEquals(
            expected = listOf(
                FollowableTopic(topic = testTopics[0], isFollowed = true),
                FollowableTopic(topic = testTopics[1], isFollowed = false),
                FollowableTopic(topic = testTopics[2], isFollowed = true),
            ),
            actual = followableTopics.first(),
        )
    }

    /**
     * This test verifies that when the use case is invoked with the sort order set to [NAME], it
     * returns a list of [FollowableTopic]s sorted alphabetically by their topic names. It
     * simulates a scenario where no topics are followed and asserts that the output list is
     * correctly sorted and that all topics are marked as not followed.
     */
    @Test
    fun whenSortOrderIsByName_topicsSortedByNameAreReturned(): TestResult = runTest {
        // Obtain a stream of followable topics, sorted by name.
        val followableTopics: Flow<List<FollowableTopic>> = useCase(
            sortBy = NAME,
        )

        // Send some test topics and their followed state.
        topicsRepository.sendTopics(topics = testTopics)
        userDataRepository.setFollowedTopicIds(followedTopicIds = setOf())

        // Check that the followable topics are sorted by the topic name.
        assertEquals(
            expected = followableTopics.first(),
            actual = testTopics
                .sortedBy { it.name }
                .map {
                    FollowableTopic(topic = it, isFollowed = false)
                },
        )
    }
}

/**
 * A list of sample topics used for testing purposes. This list provides a controlled set of
 * data that can be used to verify the behavior of the use case under different scenarios,
 * such as when topics are followed or not, or when they are sorted in a particular order.
 */
private val testTopics = listOf(
    Topic(
        id = "1",
        name = "Headlines",
        shortDescription = "",
        longDescription = "",
        url = "",
        imageUrl = "",
    ),
    Topic(
        id = "2",
        name = "Android Studio",
        shortDescription = "",
        longDescription = "",
        url = "",
        imageUrl = "",
    ),
    Topic(
        id = "3",
        name = "Compose",
        shortDescription = "",
        longDescription = "",
        url = "",
        imageUrl = "",
    ),
)
