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

package com.google.samples.apps.nowinandroid.core.data.repository

import com.google.samples.apps.nowinandroid.core.data.Syncable
import com.google.samples.apps.nowinandroid.core.data.Synchronizer
import com.google.samples.apps.nowinandroid.core.data.model.asEntity
import com.google.samples.apps.nowinandroid.core.data.model.topicCrossReferences
import com.google.samples.apps.nowinandroid.core.data.model.topicEntityShells
import com.google.samples.apps.nowinandroid.core.data.testdoubles.CollectionType
import com.google.samples.apps.nowinandroid.core.data.testdoubles.TestNewsResourceDao
import com.google.samples.apps.nowinandroid.core.data.testdoubles.TestNiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.data.testdoubles.TestTopicDao
import com.google.samples.apps.nowinandroid.core.data.testdoubles.filteredInterestsIds
import com.google.samples.apps.nowinandroid.core.data.testdoubles.nonPresentInterestsIds
import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceTopicCrossRef
import com.google.samples.apps.nowinandroid.core.database.model.PopulatedNewsResource
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.datastore.ChangeListVersions
import com.google.samples.apps.nowinandroid.core.datastore.NiaPreferencesDataSource
import com.google.samples.apps.nowinandroid.core.datastore.UserPreferences
import com.google.samples.apps.nowinandroid.core.datastore.test.InMemoryDataStore
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkChangeList
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.notifications.Notifier
import com.google.samples.apps.nowinandroid.core.testing.notifications.TestNotifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit tests for [OfflineFirstNewsRepository].
 */
class OfflineFirstNewsRepositoryTest {

    /**
     * The [TestScope] used in this test. The [UnconfinedTestDispatcher] as its `context` is similar
     * to `Dispatchers.Unconfined`: the tasks that it executes are not confined to any particular
     * thread and form an event loop; it's different in that it skips delays, as all TestDispatchers
     * do. Like `Dispatchers.Unconfined`, this one does not provide guarantees about the execution
     * order when several coroutines are queued in this dispatcher. However, we ensure that the
     * launch and async blocks at the top level of runTest are entered eagerly. This allows
     * launching child coroutines and not calling runCurrent for them to start executing.
     */
    private val testScope = TestScope(context = UnconfinedTestDispatcher())

    /**
     * The [OfflineFirstNewsRepository] is the disk storage backed implementation of the
     * [NewsRepository]. Reads are exclusively from local storage to support offline access.
     */
    private lateinit var subject: OfflineFirstNewsRepository

    /**
     * The [NiaPreferencesDataSource] used in this test. It is the Class that handles saving and
     * retrieving user preferences.
     */
    private lateinit var niaPreferencesDataSource: NiaPreferencesDataSource

    /**
     * The [TestNewsResourceDao] used in this test. It is the Test double for [NewsResourceDao]
     * which is the DAO for [NewsResource] and [NewsResourceEntity] access.
     */
    private lateinit var newsResourceDao: TestNewsResourceDao

    /**
     * The [TestTopicDao] used in this test. It is Test double for [TopicDao] which is the DAO for
     * [TopicEntity] access.
     */
    private lateinit var topicDao: TestTopicDao

    /**
     * The [TestNiaNetworkDataSource] used in this test. It is the Test double for the
     * [NiaNetworkDataSource] network data source which is the Interface representing network calls
     * to the NIA backend,
     */
    private lateinit var network: TestNiaNetworkDataSource

    /**
     * The [TestNotifier] used in this test. It is the Test double for the [Notifier] which is the
     * Interface for creating notifications in the app.
     */
    private lateinit var notifier: TestNotifier

    /**
     * The [Synchronizer] that the [OfflineFirstNewsRepository] uses to keep track of the latest
     * version of the data that it has. It is the Interface marker for a class that manages
     * synchronization between local data and a remote source for a [Syncable].
     */
    private lateinit var synchronizer: Synchronizer

    /**
     * Sets up the test dependencies for the [OfflineFirstNewsRepositoryTest]. The @Before annotation
     * ensures that this function is executed before each test.
     *
     * This function initializes the following dependencies:
     *  - [niaPreferencesDataSource]: A [NiaPreferencesDataSource] instance using an [InMemoryDataStore]
     *  as its `userPreferences` property whose `initialValue` is [UserPreferences.getDefaultInstance]
     *  - [newsResourceDao]: A [TestNewsResourceDao] instance.
     *  - [topicDao]: A [TestTopicDao] instance.
     *  - [network]: A [TestNiaNetworkDataSource] instance.
     *  - [notifier]: A [TestNotifier] instance.
     *  - [synchronizer]: A [TestSynchronizer] instance, initialized with the [NiaPreferencesDataSource]
     *  property [niaPreferencesDataSource].
     *  - [subject]: The [OfflineFirstNewsRepository] instance being tested, initialized with the
     *  [niaPreferencesDataSource], [newsResourceDao], [topicDao], [network], and [notifier]
     *  properties as its properties.
     */
    @Before
    fun setup() {
        niaPreferencesDataSource = NiaPreferencesDataSource(
            userPreferences = InMemoryDataStore(initialValue = UserPreferences.getDefaultInstance()),
        )
        newsResourceDao = TestNewsResourceDao()
        topicDao = TestTopicDao()
        network = TestNiaNetworkDataSource()
        notifier = TestNotifier()
        synchronizer = TestSynchronizer(
            niaPreferencesDataSource,
        )

        subject = OfflineFirstNewsRepository(
            niaPreferencesDataSource = niaPreferencesDataSource,
            newsResourceDao = newsResourceDao,
            topicDao = topicDao,
            network = network,
            notifier = notifier,
        )
    }

    /**
     * Test that the [OfflineFirstNewsRepository.getNewsResources] stream is backed by the
     * [NewsResourceDao.getNewsResources] stream.
     *
     * This test verifies that when the [OfflineFirstNewsRepository.syncWith] method is called,
     * the [OfflineFirstNewsRepository.getNewsResources] stream emits the same list of news resources
     * as the [NewsResourceDao.getNewsResources] stream.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - Sync the [OfflineFirstNewsRepository] property [subject] using the [Synchronizer] property
     *  [synchronizer].
     *  - call [assertEquals] to verify that the `expected` argument the [Flow] of [List] of
     *  [PopulatedNewsResource] returned by [OfflineFirstNewsRepository.getNewsResources] whose
     *  [Flow.first] is collected then fed to the [Iterable.map] whose `transform` argument is
     *  the [PopulatedNewsResource.asExternalModel] function which maps the [PopulatedNewsResource]
     *  to [NewsResource] **matches** the `actual` argument of the [Flow] of [List] of [NewsResource]
     *  returned by [OfflineFirstNewsRepository.getNewsResources] whose [Flow.first] is collected.
     */
    @Test
    fun offlineFirstNewsRepository_news_resources_stream_is_backed_by_news_resource_dao(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer = synchronizer)
            assertEquals(
                expected = newsResourceDao.getNewsResources()
                    .first()
                    .map(PopulatedNewsResource::asExternalModel),
                actual = subject.getNewsResources()
                    .first(),
            )
        }

    /**
     * Test that the [OfflineFirstNewsRepository.getNewsResources] stream for a particular topic is
     * backed by the [NewsResourceDao.getNewsResources] stream.
     *
     * This test verifies that when the [OfflineFirstNewsRepository.getNewsResources] method is
     * called with a [NewsResourceQuery] that specifies a `filterTopicIds` [Set], the returned stream
     * emits the same list of news resources as the [NewsResourceDao.getNewsResources] stream when
     * called with the same `filterTopicIds` and `useFilterTopicIds` set to `true`.
     *
     * It also verifies that if the `filterTopicIds` in the [NewsResourceQuery] does not match any
     * topics in the database, the returned stream emits an empty list.
     */
    @Test
    fun offlineFirstNewsRepository_news_resources_for_topic_is_backed_by_news_resource_dao(): TestResult =
        testScope.runTest {
            assertEquals(
                expected = newsResourceDao.getNewsResources(
                    filterTopicIds = filteredInterestsIds,
                    useFilterTopicIds = true,
                )
                    .first()
                    .map(PopulatedNewsResource::asExternalModel),
                actual = subject.getNewsResources(
                    query = NewsResourceQuery(
                        filterTopicIds = filteredInterestsIds,
                    ),
                )
                    .first(),
            )

            assertEquals(
                expected = emptyList(),
                actual = subject.getNewsResources(
                    query = NewsResourceQuery(
                        filterTopicIds = nonPresentInterestsIds,
                    ),
                )
                    .first(),
            )
        }

    /**
     * Test that when [OfflineFirstNewsRepository.syncWith] is called, the data from the network
     * is pulled and saved into the database.
     *
     * This test verifies the following:
     *  - The news resources in the database match the news resources from the network after sync.
     *  - The version of the news resources in the database is updated to the latest version from
     *  the network after sync.
     *  - The notifier is not called because the user has not onboarded yet.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - call the [NiaPreferencesDataSource.setShouldHideOnboarding] function of our
     *  [NiaPreferencesDataSource] property [niaPreferencesDataSource] to set the
     *  `shouldHideOnboarding` property to `false` to indicate that the user has not onboarded yet.
     *  - call the [OfflineFirstNewsRepository.syncWith] function of our [OfflineFirstNewsRepository]
     *  property [subject] to sync the data from the network into the database using our
     *  [Synchronizer] property [synchronizer] as the `synchronizer`.
     *  - initialize our [List] of [NewsResource] variable `newsResourcesFromNetwork` to the result
     *  of feeding the [List] of [String] returned by the [TestNiaNetworkDataSource.getNewsResources]
     *  of our [TestNiaNetworkDataSource] property [network] though an [Iterable.map] whose
     *  `transform` argument is the [NetworkNewsResource.asEntity] function which produces a [List]
     *  of [NewsResourceEntity] which is then fed to an [Iterable.map] whose `transform` argument is
     *  [NewsResourceEntity.asExternalModel] which produces a [List] of [NewsResource].
     *  - initialize our [List] of [NewsResource] variable `newsResourcesFromDb` to the result of
     *  feeding the [List] of [PopulatedNewsResource] returned by [Flow.first] applied to the
     *  [Flow] of [List] of [PopulatedNewsResource] returned by the [NewsResourceDao.getNewsResources]
     *  of our [NewsResourceDao] property [newsResourceDao] which is then fed to an [Iterable.map]
     *  whose `transform` argument is the [PopulatedNewsResource.asExternalModel] function which
     *  produces a [List] of [NewsResource].
     *  - call [assertEquals] to verify that the `expected` argument, the [List] of [String] that
     *  results from feeding the [List] of [NewsResource] `newsResourcesFromNetwork` to [Iterable.map]
     *  with `transform` set to [NewsResource.id] sorted by [Iterable.sorted] is the **same** as the
     *  `actual` argument the [List] of [String] that results from feeding the [List] of [NewsResource]
     *  variable `newsResourcesFromDb` to [Iterable.map] with its `transform` set to [NewsResource.id]
     *  sorted by [Iterable.sorted].
     *  - call [assertEquals] to verify that the `expected` argument the [Int] that results from
     *  calling the [TestNiaNetworkDataSource.latestChangeListVersion] method of our
     *  [TestNiaNetworkDataSource] property [network] with the `collectionType` argument set to
     *  [CollectionType.NewsResources] is the **same** as the `actual` argument the [Int]
     *  that results from accessing the [ChangeListVersions.newsResourceVersion] property of the
     *  [ChangeListVersions] returned by the [Synchronizer.getChangeListVersions] method of our
     *  [Synchronizer] property [synchronizer].
     *  - call [assertTrue] to verify that the `actual` argument the [Boolean] that results from
     *  calling the [List.isEmpty] method of the [List] of [List] of [NewsResource] that the
     *  [TestNotifier.addedNewsResources] property of our [TestNotifier] property [notifier] is
     *  `true` (meaning that the notifier has **not** been called).
     */
    @Test
    fun offlineFirstNewsRepository_sync_pulls_from_network(): TestResult =
        testScope.runTest {
            // User has not onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding = false)
            subject.syncWith(synchronizer = synchronizer)

            val newsResourcesFromNetwork: List<NewsResource> = network.getNewsResources()
                .map(transform = NetworkNewsResource::asEntity)
                .map(transform = NewsResourceEntity::asExternalModel)

            val newsResourcesFromDb: List<NewsResource> = newsResourceDao.getNewsResources()
                .first()
                .map(transform = PopulatedNewsResource::asExternalModel)

            assertEquals(
                expected = newsResourcesFromNetwork.map(transform = NewsResource::id).sorted(),
                actual = newsResourcesFromDb.map(transform = NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(collectionType = CollectionType.NewsResources),
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(actual = notifier.addedNewsResources.isEmpty())
        }

    /**
     * Test that when [OfflineFirstNewsRepository.syncWith] is called, items marked as deleted on
     * the network are deleted locally.
     *
     * This test verifies the following:
     *  - News resources that are marked as deleted on the network are deleted from the local
     *  database after sync.
     *  - The version of the news resources in the database is updated to the latest version from
     *  the network after sync.
     *  - The notifier is not called because the user has not onboarded yet.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - call the [NiaPreferencesDataSource.setShouldHideOnboarding] function of our
     *  [NiaPreferencesDataSource] property [niaPreferencesDataSource] to set the
     *  `shouldHideOnboarding` property to `false` to indicate that the user has not onboarded yet.
     *  - initialize our [List] of [NewsResource] variable `newsResourcesFromNetwork` to the result
     *  of feeding the [List] of [NetworkNewsResource] returned by the
     *  [TestNiaNetworkDataSource.getNewsResources] of our [TestNiaNetworkDataSource] property
     *  [network] to an [Iterable.map] whose `transform` argument is the [NetworkNewsResource.asEntity]
     *  method which produces a [List] of [NewsResourceEntity] which is then fed to an [Iterable.map]
     *  whose `transform` argument is the [NewsResourceEntity.asExternalModel] method which produces
     *  a [List] of [NewsResource].
     *  - initialize our [Set] of [String] variable `deletedItems` to the result of feeding the
     *  [List] of [NewsResource] that results from feeding the [List] of [NewsResource] variable
     *  `newsResourcesFromNetwork` to an [Iterable.map] whose `transform` argument is [NewsResource.id]
     *  then feeding that [List] of [String] to a [Iterable.partition] which splits the [List] of
     *  [String] into a [Pair] based on the `predicate` argument which is a lambda in which it
     *  converts the characters in each [String] to [Int], sums them, and checks if the sum is
     *  even, selects the [Pair.first] of the [Pair], then converts the [List] of [String] to a
     *  [Set] using its [Iterable.toSet]
     *  - iterate over the [Set] of [String] variable `deletedItems` using its [Iterable.forEach]
     *  method and in the `action` lambda argument we capture the [String] passed the lambda in
     *  variable `deleted` then call the [TestNiaNetworkDataSource.editCollection] method
     *  of our [TestNiaNetworkDataSource] property [network] with its `collectionType` argument set
     *  to [CollectionType.NewsResources], its `id` argument set to the [String] variable `deleted`,
     *  and its `isDelete` argument set to `true`.
     *  - call the [OfflineFirstNewsRepository.syncWith] function of our [OfflineFirstNewsRepository]
     *  property [subject] to sync the data from the network into the database using our
     *  [Synchronizer] property [synchronizer] as the `synchronizer`.
     *  - initialize our [List] of [NewsResource] variable `newsResourcesFromDb` to the result of
     *  feeding the [Flow] of [List] of [PopulatedNewsResource] returned by the
     *  [NewsResourceDao.getNewsResources] property [newsResourceDao] to [Flow.first] to select the
     *  first [List] of [PopulatedNewsResource] and feeding that to an [Iterable.map] whose `transform`
     *  argument is [PopulatedNewsResource.asExternalModel] which produces a [List] of [NewsResource].
     *  - call [assertEquals] to verify that the `expected` argument the [List] of [String] that
     *  results from feeding the [List] of [NewsResource] variable `newsResourcesFromDb` to an
     *  [Iterable.map] whose `transform` argument is [NewsResource.id], subtracting the [Set]
     *  of [String] variable `deletedItems` and sorting the result is the **same** as the `actual`
     *  argument the [List] of [String] that results from feeding the [List] of [NewsResource]
     *  variable `newsResourcesFromDb` to an [Iterable.map] whose `transform` argument is
     *  [NewsResource.id], sorted.
     *  - call [assertEquals] to verify that the `expected` argument the [Int] that results from
     *  calling the [TestNiaNetworkDataSource.latestChangeListVersion] method of the
     *  [TestNiaNetworkDataSource] property [network] with its `collectionType` argument set to
     *  [CollectionType.NewsResources] is the **same** as the `actual` argument the [Int] that
     *  results from calling the [Synchronizer.getChangeListVersions] method of the
     *  [Synchronizer] property [synchronizer] and returning its
     *  [ChangeListVersions.newsResourceVersion] property.
     *  - call [assertTrue] to verify that the `actual` argument the [Boolean] that results from
     *  calling the [List.isEmpty] method of the [List] of [List] of [NewsResource] that the
     *  [TestNotifier.addedNewsResources] property of our [TestNotifier] property [notifier] is
     *  `true` (meaning that the notifier has **not** been called).
     */
    @Test
    fun offlineFirstNewsRepository_sync_deletes_items_marked_deleted_on_network(): TestResult =
        testScope.runTest {
            // User has not onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding = false)

            val newsResourcesFromNetwork: List<NewsResource> = network.getNewsResources()
                .map(transform = NetworkNewsResource::asEntity)
                .map(transform = NewsResourceEntity::asExternalModel)

            // Delete half of the items on the network
            val deletedItems: Set<String> = newsResourcesFromNetwork
                .map(transform = NewsResource::id)
                .partition { it.chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach { deleted: String ->
                network.editCollection(
                    collectionType = CollectionType.NewsResources,
                    id = deleted,
                    isDelete = true,
                )
            }

            subject.syncWith(synchronizer = synchronizer)

            val newsResourcesFromDb: List<NewsResource> = newsResourceDao.getNewsResources()
                .first()
                .map(transform = PopulatedNewsResource::asExternalModel)

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                expected = (newsResourcesFromNetwork
                    .map(transform = NewsResource::id) - deletedItems)
                    .sorted(),
                actual = newsResourcesFromDb.map(transform = NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(CollectionType.NewsResources),
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(actual = notifier.addedNewsResources.isEmpty())
        }

    /**
     * Test that when [OfflineFirstNewsRepository.syncWith] is called with an existing news version,
     * only the news resources with a newer version are pulled from the network and saved into the
     * database.
     *
     * This test verifies the following:
     *  - Only news resources with a newer version than the current version in the database are pulled
     *  from the network and saved into the database after sync.
     *  - The version of the news resources in the database is updated to the latest version from
     *  the network after sync.
     *  - The notifier is not called because the user has not onboarded yet.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - call the [NiaPreferencesDataSource.setShouldHideOnboarding] function of our
     *  [NiaPreferencesDataSource] property [niaPreferencesDataSource] to set the
     *  `shouldHideOnboarding` property to `false` to indicate that the user has not onboarded yet.
     *  - call the [TestSynchronizer.updateChangeListVersions] function of our [TestSynchronizer]
     *  property [synchronizer] to set the `newsResourceVersion` property of the current
     *  [ChangeListVersions] to 7.
     *  - call the [OfflineFirstNewsRepository.syncWith] function of our [OfflineFirstNewsRepository]
     *  property [subject] to sync the data from the network into the database using our
     *  [Synchronizer] property [synchronizer] as the `synchronizer`.
     *  - initialize our [List] of [NetworkChangeList] variable `val changeList` to the result of
     *  calling the [TestNiaNetworkDataSource.changeListsAfter] method of our [TestNiaNetworkDataSource]
     *  property [network] with the `collectionType` argument set to [CollectionType.NewsResources]
     *  and the `version` argument set to 7.
     *  - initialize our [Set] of [String] variable `val changeListIds` to the result of feeding the
     *  [List] of [NetworkChangeList] variable `changeList` to an [Iterable.map] whose `transform`
     *  argument is [NetworkChangeList.id] and feeding the resulting [List] of [String] to an
     *  [Iterable.toSet] to convert it to a [Set].
     *  - initialize our [List] of [NewsResource] variable `val newsResourcesFromNetwork` to the
     *  result of feeding the [List] of [NetworkNewsResource] returned by the
     *  [TestNiaNetworkDataSource.getNewsResources] method of our [TestNiaNetworkDataSource] property
     *  [network] to an [Iterable.map] whose `transform` argument is the [NetworkNewsResource.asEntity]
     *  to convert it to a [List] of [NewsResourceEntity] which is then fed to an [Iterable.map] whose
     *  `transform` argument is the [NewsResourceEntity.asExternalModel] method to convert it to a
     *  [List] of [NewsResource], then feeding that to an [Iterable.filter] whose `predicate` argument
     *  selects all of the [NewsResource] in the [List] that have an id in the [Set] of [String]
     *  variable `changeListIds`.
     *  - initialize our [List] of [NewsResource] variable `val newsResourcesFromDb` to the result
     *  of feeding the [List] of [PopulatedNewsResource] returned by the
     *  [NewsResourceDao.getNewsResources] method of [NewsResourceDao] property [newsResourceDao] to
     *  [Flow.first] to select the first [List] of [PopulatedNewsResource] and feeding that to an
     *  [Iterable.map] whose `transform` argument is the [PopulatedNewsResource.asExternalModel]
     *  method to convert it to a [List] of [NewsResource].
     *  - call [assertEquals] to verify that the `expected` argument the [List] of [String] that
     *  results from feeding the [List] of [NewsResource] variable `newsResourcesFromDb` to an
     *  [Iterable.map] whose `transform` argument is [NewsResource.id] sorted is the **same** as
     *  the `actual` argument the [List] of [String] that results from feeding the [List] of
     *  [NewsResource] variable `newsResourcesFromNetwork` to an [Iterable.map] whose `transform`
     *  argument is [NewsResource.id] sorted.
     *  - call [assertEquals] to verify that the `expected` argument the [Int] that results from
     *  calling the [List.last] method of the [List] of [NetworkChangeList] variable `changeList`
     *  and returing its [NetworkChangeList.changeListVersion] property is the **same** as the
     *  `actual` argument the [Int] that results from calling the [Synchronizer.getChangeListVersions]
     *  method of the [Synchronizer] property [synchronizer] and returning its
     *  [ChangeListVersions.newsResourceVersion] property.
     *  - call [assertTrue] to verify that the `actual` argument the [Boolean] that results from
     *  calling the [List.isEmpty] method of the [List] of [List] of [NewsResource] that the
     *  [TestNotifier.addedNewsResources] property of our [TestNotifier] property [notifier] is
     *  `true` (meaning that the notifier has **not** been called).
     */
    @Test
    fun offlineFirstNewsRepository_incremental_sync_pulls_from_network(): TestResult =
        testScope.runTest {
            // User has not onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding = false)

            // Set news version to 7
            synchronizer.updateChangeListVersions {
                copy(newsResourceVersion = 7)
            }

            subject.syncWith(synchronizer = synchronizer)

            val changeList: List<NetworkChangeList> = network.changeListsAfter(
                collectionType = CollectionType.NewsResources,
                version = 7,
            )
            val changeListIds: Set<String> = changeList
                .map(transform = NetworkChangeList::id)
                .toSet()

            val newsResourcesFromNetwork: List<NewsResource> = network.getNewsResources()
                .map(transform = NetworkNewsResource::asEntity)
                .map(transform = NewsResourceEntity::asExternalModel)
                .filter { it.id in changeListIds }

            val newsResourcesFromDb: List<NewsResource> = newsResourceDao.getNewsResources()
                .first()
                .map(transform = PopulatedNewsResource::asExternalModel)

            assertEquals(
                expected = newsResourcesFromNetwork.map(transform = NewsResource::id).sorted(),
                actual = newsResourcesFromDb.map(transform = NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = changeList.last().changeListVersion,
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(actual = notifier.addedNewsResources.isEmpty())
        }

    /**
     * Test that when [OfflineFirstNewsRepository.syncWith] is called, the shell topic entities are
     * saved to the database.
     *
     * This test verifies that the topic entities in the database match the topic entities from the
     * network news resources after sync.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - call the [OfflineFirstNewsRepository.syncWith] function of our [OfflineFirstNewsRepository]
     *  property [subject] to sync the data from the network into the database using our
     *  [Synchronizer] property [synchronizer] as the `synchronizer`.
     *  - call [assertEquals] to verify that the `expected` argument the [List] of [TopicEntity] that
     *  results from feeding the [List] of [NetworkNewsResource] that the
     *  [TestNiaNetworkDataSource.getNewsResources] method of our [TestNiaNetworkDataSource] property
     *  [network] returns to an [Iterable.map] whose `transform` argument is the
     *  [NetworkNewsResource.topicEntityShells] function converting each to a [TopicEntity], then
     *  flattening the resulting [List] of [List] of [TopicEntity] by calling the [Iterable.flatten]
     *  extension function, then calling the [Iterable.distinctBy] extension function with its
     *  `selector` argument set to the [TopicEntity.id] property, then sorting the resulting [List]
     *  of [TopicEntity] using the [Iterable.sortedBy] extension function with its `selector` argument
     *  set to the [TopicEntity.toString] method **matches** the `actual` argument the [List] of
     *  [TopicEntity] that results from feeding the [List] of [TopicEntity] returned by collecting
     *  the [Flow.first] emitted by the [Flow] of [List] of [TopicEntity] returned by the
     *  [TopicDao.getTopicEntities] method of our [TestTopicDao] property [topicDao] then sorting the
     *  resulting [List] of [TopicEntity] using the [Iterable.sortedBy] extension function with its
     *  `selector` argument set to the [NewsResourceTopicCrossRef.toString] method.
     */
    @Test
    fun offlineFirstNewsRepository_sync_saves_shell_topic_entities(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer = synchronizer)

            assertEquals(
                expected = network.getNewsResources()
                    .map(transform = NetworkNewsResource::topicEntityShells)
                    .flatten()
                    .distinctBy(selector = TopicEntity::id)
                    .sortedBy(selector = TopicEntity::toString),
                actual = topicDao.getTopicEntities()
                    .first()
                    .sortedBy(selector = TopicEntity::toString),
            )
        }

    /**
     * Test that when [OfflineFirstNewsRepository.syncWith] is called, the topic cross references
     * are saved to the database.
     *
     * This test verifies that the topic cross references in the database match the topic cross
     * references from the network news resources after sync.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - call the [OfflineFirstNewsRepository.syncWith] function of our [OfflineFirstNewsRepository]
     *  property [subject] to sync the data from the network into the database using our
     *  [Synchronizer] property [synchronizer] as the `synchronizer`.
     *  - call [assertEquals] to verify that the `expected` argument the [List] of
     *  [NewsResourceTopicCrossRef] that results from feeding the [List] of [NetworkNewsResource] that
     *  the [TestNiaNetworkDataSource.getNewsResources] method of our [TestNiaNetworkDataSource]
     *  property [network] returns to the [Iterable.map] whose `transform` argument is the
     *  [NetworkNewsResource.topicCrossReferences] function, then flattening the resulting [List] of
     *  [List] of [NewsResourceTopicCrossRef] by calling the [Iterable.flatten] extension function,
     *  then calling the [Iterable.distinct] extension function, then sorting the resulting [List]
     *  of [NewsResourceTopicCrossRef] using the [Iterable.sortedBy] extension function with its
     *  `selector` argument set to the [NewsResourceTopicCrossRef.toString] method **matches** the
     *  `actual` argument the [List] of [NewsResourceTopicCrossRef] that results from feeding the
     *  [List] of [NewsResourceTopicCrossRef] returned by the [TestNewsResourceDao.topicCrossReferences]
     *  property of our [TestNewsResourceDao] property [newsResourceDao] to the [Iterable.sortedBy]
     *  extension function with its `selector` argument set to the [NewsResourceTopicCrossRef.toString]
     *  method.
     */
    @Test
    fun offlineFirstNewsRepository_sync_saves_topic_cross_references(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer = synchronizer)

            assertEquals(
                expected = network.getNewsResources()
                    .map(transform = NetworkNewsResource::topicCrossReferences)
                    .flatten()
                    .distinct()
                    .sortedBy(selector = NewsResourceTopicCrossRef::toString),
                actual = newsResourceDao.topicCrossReferences
                    .sortedBy(selector = NewsResourceTopicCrossRef::toString),
            )
        }

    /**
     * Test that when [OfflineFirstNewsRepository.syncWith] is called for the first time, all news
     * resources from the network are marked as read.
     *
     * This test verifies that after the first sync, the set of viewed news resources in the
     * [NiaPreferencesDataSource] matches the set of news resources from the network.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - call the [OfflineFirstNewsRepository.syncWith] function of our [OfflineFirstNewsRepository]
     *  property [subject] to sync the data from the network into the database using our
     *  [Synchronizer] property [synchronizer] as the `synchronizer`.
     *  - call [assertEquals] to verify that the `expected` argument the [Set] of [String] that
     *  results from feeding the [List] of [NetworkNewsResource] that the
     *  [TestNiaNetworkDataSource.getNewsResources] method of our [TestNiaNetworkDataSource] property
     *  [network] returns to an [Iterable.map] whose `transform` argument is the [NetworkNewsResource.id]
     *  property then feeding the resulting [List] of [String] to the [Iterable.toSet] extension
     *  function **matches** the `actual` argument the [Set] of [String] that results from collecting
     *  the [Flow.first] emitted by the [Flow] of [UserData] that the
     *  [NiaPreferencesDataSource.userData] property of our [NiaPreferencesDataSource] property
     *  [niaPreferencesDataSource] returns and returning its [UserData.viewedNewsResources] property.
     */
    @Test
    fun offlineFirstNewsRepository_sync_marks_as_read_on_first_run(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer = synchronizer)

            assertEquals(
                expected = network.getNewsResources().map { it.id }.toSet(),
                actual = niaPreferencesDataSource.userData.first().viewedNewsResources,
            )
        }

    /**
     * Test that when [OfflineFirstNewsRepository.syncWith] is called on a subsequent run
     * (meaning that the [ChangeListVersions.newsResourceVersion] property of the `currentValues`
     * of the [TestSynchronizer.updateChangeListVersions] method of the [TestSynchronizer] property
     * [synchronizer] is not 0), then no new news resources will be marked as "viewed" in the
     * [NiaPreferencesDataSource.userData] property of the [NiaPreferencesDataSource] property
     * [niaPreferencesDataSource].
     *
     * This test verifies the following:
     *  - If the news resources in the database are already up-to-date with the network, no new
     *  news resources will be marked as "viewed".
     *  - The [Set] of [String] that the [UserData.viewedNewsResources] property of the
     *  [UserPreferences] which is the [Flow.first] emitted by the [Flow] of [UserPreferences]
     *  returned by the [NiaPreferencesDataSource.userData] property of the
     *  [NiaPreferencesDataSource] property [niaPreferencesDataSource] will be an [emptySet].
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - call the [TestSynchronizer.updateChangeListVersions] function of our [TestSynchronizer]
     *  property [synchronizer] to set the `newsResourceVersion` property of the current
     *  [ChangeListVersions] to 7.
     *  - call the [OfflineFirstNewsRepository.syncWith] function of our [OfflineFirstNewsRepository]
     *  property [subject] to sync the data from the network into the database using our
     *  [Synchronizer] property [synchronizer] as the `synchronizer`.
     *  - call [assertEquals] to verify that the `expected` argument which is an [emptySet] of
     *  [String] **matches** the `actual` argument the [Set] of [String] that results from
     *  collecting the [Flow.first] emitted by the [Flow] of [UserData] that the
     *  [NiaPreferencesDataSource.userData] property of our [NiaPreferencesDataSource] property
     *  [niaPreferencesDataSource] returns and returning its [UserData.viewedNewsResources] property.
     *
     * TODO: Continue here.
     */
    @Test
    fun offlineFirstNewsRepository_sync_does_not_mark_as_read_on_subsequent_run(): TestResult =
        testScope.runTest {
            // Pretend that we already have up to change list 7
            synchronizer.updateChangeListVersions {
                copy(newsResourceVersion = 7)
            }

            subject.syncWith(synchronizer = synchronizer)

            assertEquals(
                expected = emptySet(),
                actual = niaPreferencesDataSource.userData.first().viewedNewsResources,
            )
        }

    @Test
    fun offlineFirstNewsRepository_sends_notifications_for_newly_synced_news_that_is_followed(): TestResult =
        testScope.runTest {
            // User has onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding = true)

            val networkNewsResources = network.getNewsResources()

            // Follow roughly half the topics
            val followedTopicIds: Set<String> = networkNewsResources
                .flatMap(transform = NetworkNewsResource::topicEntityShells)
                .mapNotNull { topic: TopicEntity ->
                    when (topic.id.chars().sum() % 2) {
                        0 -> topic.id
                        else -> null
                    }
                }
                .toSet()

            // Set followed topics
            niaPreferencesDataSource.setFollowedTopicIds(topicIds = followedTopicIds)

            subject.syncWith(synchronizer = synchronizer)

            val followedNewsResourceIdsFromNetwork: List<String> = networkNewsResources
                .filter { (it.topics intersect followedTopicIds).isNotEmpty() }
                .map(transform = NetworkNewsResource::id)
                .sorted()

            // Notifier should have been called with only news resources that have topics
            // that the user follows
            assertEquals(
                expected = followedNewsResourceIdsFromNetwork,
                actual = notifier.addedNewsResources
                    .first()
                    .map(transform = NewsResource::id)
                    .sorted(),
            )
        }

    @Test
    fun offlineFirstNewsRepository_does_not_send_notifications_for_existing_news_resources(): TestResult =
        testScope.runTest {
            // User has onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding = true)

            val networkNewsResources: List<NewsResourceEntity> = network.getNewsResources()
                .map(transform = NetworkNewsResource::asEntity)

            val newsResources: List<NewsResource> = networkNewsResources
                .map(transform = NewsResourceEntity::asExternalModel)

            // Prepopulate dao with news resources
            newsResourceDao.upsertNewsResources(newsResourceEntities = networkNewsResources)

            val followedTopicIds = newsResources
                .flatMap(transform = NewsResource::topics)
                .map(transform = Topic::id)
                .toSet()

            // Follow all topics
            niaPreferencesDataSource.setFollowedTopicIds(topicIds = followedTopicIds)

            subject.syncWith(synchronizer = synchronizer)

            // Notifier should not have been called bc all news resources existed previously
            assertTrue(actual = notifier.addedNewsResources.isEmpty())
        }
}
