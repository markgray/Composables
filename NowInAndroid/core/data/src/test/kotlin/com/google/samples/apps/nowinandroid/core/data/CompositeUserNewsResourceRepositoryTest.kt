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

package com.google.samples.apps.nowinandroid.core.data

import com.google.samples.apps.nowinandroid.core.data.repository.CompositeUserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsResourceQuery
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.model.data.UserNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.mapToUserNewsResources
import com.google.samples.apps.nowinandroid.core.testing.repository.TestNewsRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.TestUserDataRepository
import com.google.samples.apps.nowinandroid.core.testing.repository.emptyUserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [CompositeUserNewsResourceRepository].
 */
class CompositeUserNewsResourceRepositoryTest {

    /**
     * A test implementation of the [NewsRepository] that can be used to simulate different
     * scenarios in tests.
     */
    private val newsRepository = TestNewsRepository()

    /**
     * A test implementation of the [UserDataRepository] that will be used by the
     * [CompositeUserNewsResourceRepository] property [userNewsResourceRepository].
     */
    private val userDataRepository = TestUserDataRepository()

    /**
     * The test user news resource repository that is being tested.
     * This is the entry point for observing user news resources.
     */
    private val userNewsResourceRepository = CompositeUserNewsResourceRepository(
        newsRepository = newsRepository,
        userDataRepository = userDataRepository,
    )

    /**
     * When the `observeAll` method is called without any filters, all news resources should
     * be returned. The news resources should be mapped to [UserNewsResource]s, with the
     * [UserNewsResource.isSaved] field set to `true` if the user has bookmarked the resource,
     * and `false` otherwise.
     *
     * We call [runTest] to run its [TestScope] `testBody` suspend lambda argument in which we:
     *  - Initialize our [Flow] of [List] of [UserNewsResource] variable `userNewsResources` to the
     *  return value of the [CompositeUserNewsResourceRepository.observeAll] method of our
     *  [CompositeUserNewsResourceRepository] property [userNewsResourceRepository] (Returns all
     *  available news resources joined with user data).
     *  - We call the [TestNewsRepository.sendNewsResources] method of our [TestNewsRepository]
     *  property [newsRepository] to send some news resources into the repository, our [List] of
     *  [NewsResource] property [sampleNewsResources].
     *  - We initialize our [UserData] variable `userData` to a copy of the [emptyUserData] empty
     *  [UserData] object with its [UserData.bookmarkedNewsResources] field set to a set of the
     *  first two news resource ids in [sampleNewsResources], and its [UserData.followedTopics]
     *  field set to a set containing only the [Topic.id] of [sampleTopic1].
     *  - We call the [TestUserDataRepository.setUserData] method of our [TestUserDataRepository]
     *  property [userDataRepository] to set the user data to [UserData] variable `userData`.
     *  - We call the [assertEquals] method to verify that its `expected` argument, the [List] of
     *  [UserNewsResource] returned by the [mapToUserNewsResources] method when applied to the
     *  [List] of [NewsResource] property [sampleNewsResources] with its `userData` argument set
     *  to [UserData] variable `userData` **matches** the `actual` argument, the [List] of
     *  [UserNewsResource] returned by the [Flow.first] method of [Flow] of [List] of
     *  [UserNewsResource] variable `userNewsResources`.
     */
    @Test
    fun whenNoFilters_allNewsResourcesAreReturned(): TestResult = runTest {
        // Obtain the user news resources flow.
        val userNewsResources: Flow<List<UserNewsResource>> =
            userNewsResourceRepository.observeAll()

        // Send some news resources and user data into the data repositories.
        newsRepository.sendNewsResources(newsResources = sampleNewsResources)

        // Construct the test user data with bookmarks and followed topics.
        val userData: UserData = emptyUserData.copy(
            bookmarkedNewsResources = setOf(sampleNewsResources[0].id, sampleNewsResources[2].id),
            followedTopics = setOf(sampleTopic1.id),
        )

        userDataRepository.setUserData(userData = userData)

        // Check that the correct news resources are returned with their bookmarked state.
        assertEquals(
            expected = sampleNewsResources.mapToUserNewsResources(userData = userData),
            actual = userNewsResources.first(),
        )
    }

    /**
     * When the `observeAll` method is called with a filter for a specific topic ID, only
     * news resources matching that topic ID should be returned. The news resources should
     * be mapped to [UserNewsResource]s, with the [UserNewsResource.isSaved] field set to
     * `true` if the user has bookmarked the resource, and `false` otherwise.
     *
     * We call [runTest] to run its [TestScope] `testBody` suspend lambda argument in which we:
     *  - Initialize our [Flow] of [List] of [UserNewsResource] variable `userNewsResources` to the
     *  return value of the [CompositeUserNewsResourceRepository.observeAll] method of our
     *  [CompositeUserNewsResourceRepository] property [userNewsResourceRepository] when its `query`
     *  argument is a [NewsResourceQuery] whose [NewsResourceQuery.filterTopicIds] field is a set
     *  containing only the [Topic.id] of our [Topic] property [sampleTopic1].
     *  - We call the [TestNewsRepository.sendNewsResources] method of our [TestNewsRepository]
     *  property [newsRepository] to send some news resources into the repository, our [List] of
     *  [NewsResource] property [sampleNewsResources].
     *  - We call the [TestUserDataRepository.setUserData] method of our [TestUserDataRepository]
     *  property [userDataRepository] to set the user data to [emptyUserData].
     *  - We call the [assertEquals] method to verify that its `expected` argument, the [List] of
     *  [UserNewsResource] returned by the [mapToUserNewsResources] method when applied to a [List]
     *  of [NewsResource] which is the result of filtering the [List] of [NewsResource] property
     *  [sampleNewsResources] to only keep [NewsResource]s which contain [sampleTopic1] in their
     *  [NewsResource.topics] field, with its `userData` argument set to [emptyUserData] **matches**
     *  the `actual` argument, the [List] of [UserNewsResource] returned by the [Flow.first] method
     *  of [Flow] of [List] of [UserNewsResource] variable `userNewsResources`.
     */
    @Test
    fun whenFilteredByTopicId_matchingNewsResourcesAreReturned(): TestResult = runTest {
        // Obtain a stream of user news resources for the given topic id.
        val userNewsResources: Flow<List<UserNewsResource>> =
            userNewsResourceRepository.observeAll(
                query = NewsResourceQuery(
                    filterTopicIds = setOf(
                        sampleTopic1.id,
                    ),
                ),
            )

        // Send test data into the repositories.
        newsRepository.sendNewsResources(newsResources = sampleNewsResources)
        userDataRepository.setUserData(userData = emptyUserData)

        // Check that only news resources with the given topic id are returned.
        assertEquals(
            expected = sampleNewsResources
                .filter { sampleTopic1 in it.topics }
                .mapToUserNewsResources(userData = emptyUserData),
            actual = userNewsResources.first(),
        )
    }

    /**
     * When the `observeAllForFollowedTopics` method is called, only news resources matching
     * topics that the user has followed should be returned. The news resources should be mapped to
     * [UserNewsResource]s, with the [UserNewsResource.isSaved] field set to `true` if the user has
     * bookmarked the resource, and `false` otherwise.
     *
     * We call [runTest] to run its [TestScope] `testBody` suspend lambda argument in which we:
     *  - Initialize our [Flow] of [List] of [UserNewsResource] variable `userNewsResources` to the
     *  return value of the [CompositeUserNewsResourceRepository.observeAllForFollowedTopics] method
     *  of our [CompositeUserNewsResourceRepository] property [userNewsResourceRepository].
     *  - We initialize our [UserData] variable `userData` to a copy of the [emptyUserData] empty
     *  [UserData] object with its [UserData.followedTopics] field set to a set containing only
     *  the [Topic.id] of [sampleTopic1].
     *  - We call the [TestNewsRepository.sendNewsResources] method of our [TestNewsRepository]
     *  property [newsRepository] to send some news resources into the repository, our [List] of
     *  [NewsResource] property [sampleNewsResources].
     *  - We call the [TestUserDataRepository.setUserData] method of our [TestUserDataRepository]
     *  property [userDataRepository] to set the user data to [UserData] variable `userData`.
     *  - We call the [assertEquals] method to verify that its `expected` argument, the [List] of
     *  [UserNewsResource] returned by the [mapToUserNewsResources] method when applied to a [List]
     *  of [NewsResource] which is the result of filtering the [List] of [NewsResource] property
     *  [sampleNewsResources] to only keep [NewsResource]s which contain [sampleTopic1] in their
     *  [NewsResource.topics] field, with its `userData` argument set to [UserData] variable
     *  `userData` **matches** the `actual` argument, the [List] of [UserNewsResource] returned by
     *  the [Flow.first] method of [Flow] of [List] of [UserNewsResource] variable `userNewsResources`.
     */
    @Test
    fun whenFilteredByFollowedTopics_matchingNewsResourcesAreReturned(): TestResult = runTest {
        // Obtain a stream of user news resources for the given topic id.
        val userNewsResources: Flow<List<UserNewsResource>> =
            userNewsResourceRepository.observeAllForFollowedTopics()

        // Send test data into the repositories.
        val userData: UserData = emptyUserData.copy(
            followedTopics = setOf(sampleTopic1.id),
        )
        newsRepository.sendNewsResources(newsResources = sampleNewsResources)
        userDataRepository.setUserData(userData = userData)

        // Check that only news resources with the given topic id are returned.
        assertEquals(
            expected = sampleNewsResources
                .filter { sampleTopic1 in it.topics }
                .mapToUserNewsResources(userData = userData),
            actual = userNewsResources.first(),
        )
    }

    /**
     * When the `observeAllBookmarked` method is called, only news resources that have been
     * bookmarked by the user should be returned. The news resources should be mapped to
     * [UserNewsResource]s, with the [UserNewsResource.isSaved] field set to `true`.
     *
     * We call [runTest] to run its [TestScope] `testBody` suspend lambda argument in which we:
     *  - Initialize our [Flow] of [List] of [UserNewsResource] variable `userNewsResources` to the
     *  return value of the [CompositeUserNewsResourceRepository.observeAllBookmarked] method of our
     *  [CompositeUserNewsResourceRepository] property [userNewsResourceRepository] (Returns a list
     *  of news resources that have been bookmarked by the user).
     *  - We call the [TestNewsRepository.sendNewsResources] method of our [TestNewsRepository]
     *  property [newsRepository] to send some news resources into the repository, our [List] of
     *  [NewsResource] property [sampleNewsResources].
     *  - We initialize our [UserData] variable `userData` to a copy of the [emptyUserData] empty
     *  [UserData] object with its [UserData.bookmarkedNewsResources] field set to a set of the
     *  first and third news resource ids in [sampleNewsResources], and its [UserData.followedTopics]
     *  field set to a set containing only the [Topic.id] of [sampleTopic1].
     *  - We call the [TestUserDataRepository.setUserData] method of our [TestUserDataRepository]
     *  property [userDataRepository] to set the user data to [UserData] variable `userData`.
     *  - We call the [assertEquals] method to verify that its `expected` argument, the [List] of
     *  [UserNewsResource] returned by the [mapToUserNewsResources] method when applied to a [List]
     *  of [NewsResource] which is the first and third [NewsResource] in the [List] of [NewsResource]
     *  property [sampleNewsResources] with its `userData` argument set to `userData` **matches**
     *  the `actual` argument, the [List] of [UserNewsResource] returned by the [Flow.first] method
     *  of [Flow] of [List] of [UserNewsResource] variable `userNewsResources`.
     */
    @Test
    fun whenFilteredByBookmarkedResources_matchingNewsResourcesAreReturned(): TestResult = runTest {
        // Obtain the bookmarked user news resources flow.
        val userNewsResources: Flow<List<UserNewsResource>> =
            userNewsResourceRepository.observeAllBookmarked()

        // Send some news resources and user data into the data repositories.
        newsRepository.sendNewsResources(newsResources = sampleNewsResources)

        // Construct the test user data with bookmarks and followed topics.
        val userData: UserData = emptyUserData.copy(
            bookmarkedNewsResources = setOf(sampleNewsResources[0].id, sampleNewsResources[2].id),
            followedTopics = setOf(sampleTopic1.id),
        )

        userDataRepository.setUserData(userData = userData)

        // Check that the correct news resources are returned with their bookmarked state.
        assertEquals(
            expected = listOf(
                sampleNewsResources[0],
                sampleNewsResources[2],
            ).mapToUserNewsResources(userData),
            actual = userNewsResources.first(),
        )
    }
}

/**
 * A sample [Topic] that can be used for testing.
 */
private val sampleTopic1 = Topic(
    id = "Topic1",
    name = "Headlines",
    shortDescription = "",
    longDescription = "long description",
    url = "URL",
    imageUrl = "image URL",
)

/**
 *
 */
private val sampleTopic2 = Topic(
    id = "Topic2",
    name = "UI",
    shortDescription = "",
    longDescription = "long description",
    url = "URL",
    imageUrl = "image URL",
)

/**
 * A sample list of [NewsResource]s that can be used for testing.
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
        publishDate = Instant.parse("2021-11-09T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(sampleTopic1),
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
        topics = listOf(sampleTopic1, sampleTopic2),
    ),
    NewsResource(
        id = "3",
        title = "Community tip on Paging",
        content = "Tips for using the Paging library from the developer community",
        url = "https://youtu.be/r5JgIyS3t3s",
        headerImageUrl = "https://i.ytimg.com/vi/r5JgIyS3t3s/maxresdefault.jpg",
        publishDate = Instant.parse("2021-11-08T00:00:00.000Z"),
        type = "Video 📺",
        topics = listOf(sampleTopic2),
    ),
)
