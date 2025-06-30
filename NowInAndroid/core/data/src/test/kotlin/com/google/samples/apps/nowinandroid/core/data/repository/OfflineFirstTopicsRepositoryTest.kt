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
import com.google.samples.apps.nowinandroid.core.data.testdoubles.CollectionType
import com.google.samples.apps.nowinandroid.core.data.testdoubles.TestNiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.data.testdoubles.TestTopicDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.datastore.ChangeListVersions
import com.google.samples.apps.nowinandroid.core.datastore.NiaPreferencesDataSource
import com.google.samples.apps.nowinandroid.core.datastore.UserPreferences
import com.google.samples.apps.nowinandroid.core.datastore.test.InMemoryDataStore
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for [OfflineFirstTopicsRepository].
 */
class OfflineFirstTopicsRepositoryTest {
    /**
     * The [TestScope] used in this test. The [UnconfinedTestDispatcher] as its `context` is similar
     * to `Dispatchers.Unconfined`: the tasks that it executes are not confined to any particular
     * thread and form an event loop; it's different in that it skips delays, as all TestDispatchers
     * do. Like `Dispatchers.Unconfined`, this one does not provide guarantees about the execution
     * order when several coroutines are queued in this dispatcher. However, we ensure that the
     * launch and async blocks at the top level of runTest are entered eagerly. This allows
     * launching child coroutines and not calling runCurrent for them to start executing.
     */
    private val testScope = TestScope(UnconfinedTestDispatcher())

    /**
     * The [OfflineFirstNewsRepository] is the disk storage backed implementation of the
     * [NewsRepository]. Reads are exclusively from local storage to support offline access.
     */
    private lateinit var subject: OfflineFirstTopicsRepository

    /**
     * The DAO used for [TopicEntity] access. It is initialized to a new instance of [TestTopicDao]
     * by our [setup] method for each test.
     */
    private lateinit var topicDao: TopicDao

    /**
     * The [TestNiaNetworkDataSource] used in this test. It is the Test double for the
     * [NiaNetworkDataSource] network data source which is the Interface representing network calls
     * to the NIA backend,
     */
    private lateinit var network: TestNiaNetworkDataSource

    /**
     * The [NiaPreferencesDataSource] used in this test. It is the Class that handles saving and
     * retrieving user preferences.
     */
    private lateinit var niaPreferences: NiaPreferencesDataSource

    /**
     * The [Synchronizer] that the [OfflineFirstNewsRepository] uses to keep track of the latest
     * version of the data that it has. It is the Interface marker for a class that manages
     * synchronization between local data and a remote source for a [Syncable].
     */
    private lateinit var synchronizer: Synchronizer

    /**
     * Sets up the test dependencies before each test.
     */
    @Before
    fun setup() {
        topicDao = TestTopicDao()
        network = TestNiaNetworkDataSource()
        niaPreferences = NiaPreferencesDataSource(
            userPreferences = InMemoryDataStore(initialValue = UserPreferences.getDefaultInstance()),
        )
        synchronizer = TestSynchronizer(niaPreferences = niaPreferences)

        subject = OfflineFirstTopicsRepository(
            topicDao = topicDao,
            network = network,
        )
    }

    /**
     * Test to verify that the `topics` stream of our [OfflineFirstTopicsRepository] property [subject]
     * is backed by the `topics` DAO ([TestTopicDao] field `topicDao`).
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - Sync the [OfflineFirstNewsRepository] property [subject] using the [Synchronizer] property
     *  [synchronizer].
     *  - call [assertEquals] to verify that the `expected` argument the [List] of [Topic] that
     *  results from feeding the [Flow] of [List] of [TopicEntity] returned by the
     *  [TopicDao.getTopicEntities] to [Flow.first] to collect the first [List] of [TopicEntity],
     *  then feeding that to an [Iterable.map] whose `transform` lambda is the
     *  [TopicEntity.asExternalModel] function to convert each [TopicEntity] to a [Topic] **matches**
     *  the `actual` argument of the [List] of [Topic] that results from feeding the [Flow] of [List]
     *  of [Topic] returned by the [OfflineFirstTopicsRepository.getTopics] function of our
     *  [OfflineFirstTopicsRepository] property [subject] to [Flow.first] to collect the first
     *  [List] of [Topic]
     */
    @Test
    fun offlineFirstTopicsRepository_topics_stream_is_backed_by_topics_dao(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer = synchronizer)

            assertEquals(
                expected = topicDao.getTopicEntities()
                    .first()
                    .map(transform = TopicEntity::asExternalModel),
                actual = subject.getTopics()
                    .first(),
            )
        }

    /**
     * Test to verify that the [OfflineFirstTopicsRepository.syncWith] method pulls from the network.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - Sync the [OfflineFirstNewsRepository] property [subject] using the [Synchronizer] property
     *  [synchronizer].
     *  - Initialize our [List] of [TopicEntity] variable `val networkTopics` to the [List] of
     *  [TopicEntity] that results from calling the [NiaNetworkDataSource.getTopics] method of our
     *  [TestNiaNetworkDataSource] property [network] then mapping the [NetworkTopic] objects in
     *  that list to [TopicEntity] objects using the [NetworkTopic.asEntity] function.
     *  - Initialize our [List] of [TopicEntity] variable `val dbTopics` to the [List] of
     *  [TopicEntity] that results from feeding the [Flow] of [List] of [TopicEntity] returned by the
     *  [TopicDao.getTopicEntities] to [Flow.first] to collect the first [List] of [TopicEntity].
     *  - Call [assertEquals] to verify that the `expected` [List] of [String] which is produced by
     *  mapping the [TopicEntity.id] property of each [TopicEntity] in `networkTopics` **matches**
     *  the `actual` [List] of [String] which is produced by mapping the [TopicEntity.id] property
     *  of each [TopicEntity] in `dbTopics`.
     *  - Call [assertEquals] to verify that the `expected` [Int] which is returned by the
     *  [TestNiaNetworkDataSource.latestChangeListVersion] method of our [TestNiaNetworkDataSource]
     *  property [network] for the [CollectionType.Topics] **matches** the `actual` [Int] which is
     *  the [ChangeListVersions.topicVersion] property of the [ChangeListVersions] object returned by
     *  the [Synchronizer.getChangeListVersions] method of our [Synchronizer] property [synchronizer].
     */
    @Test
    fun offlineFirstTopicsRepository_sync_pulls_from_network(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer = synchronizer)

            val networkTopics: List<TopicEntity> = network.getTopics()
                .map(NetworkTopic::asEntity)

            val dbTopics: List<TopicEntity> = topicDao.getTopicEntities()
                .first()

            assertEquals(
                expected = networkTopics.map(transform = TopicEntity::id),
                actual = dbTopics.map(transform = TopicEntity::id),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(collectionType = CollectionType.Topics),
                actual = synchronizer.getChangeListVersions().topicVersion,
            )
        }

    /**
     * Test to verify that an incremental sync of our [OfflineFirstTopicsRepository] pulls from the
     * network.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - Set the `topicVersion` field of the [ChangeListVersions] object stored by our [Synchronizer]
     *  property [synchronizer] to 10.
     *  - Sync the [OfflineFirstNewsRepository] property [subject] using the [Synchronizer] property
     *  [synchronizer].
     *  - Initialize our [List] of [TopicEntity] variable `val networkTopics` to the [List] of
     *  [TopicEntity] that results from calling the [NiaNetworkDataSource.getTopics] method of our
     *  [TestNiaNetworkDataSource] property [network] then converting the [NetworkTopic] objects in
     *  the [List] of [NetworkTopic] returned to [TopicEntity] objects using an [Iterable.map] whose
     *  `tranform` argument is the [NetworkTopic.asEntity] function, then dropping the first 10 items
     *  to simulate the first 10 items being unchanged.
     *  - Initialize our [List] of [TopicEntity] variable `val dbTopics` to the [List] of
     *  [TopicEntity] that results from feeding the [Flow] of [List] of [TopicEntity] returned by the
     *  [TopicDao.getTopicEntities] to [Flow.first] to collect the first [List] of [TopicEntity].
     *  - Call [assertEquals] to verify that the `expected` [List] of [String] which is produced by
     *  mapping the [TopicEntity.id] property of each [TopicEntity] in `networkTopics` **matches**
     *  the `actual` [List] of [String] which is produced by mapping the [TopicEntity.id] property
     *  of each [TopicEntity] in `dbTopics`.
     *  - Call [assertEquals] to verify that the `expected` [Int] which is returned by the
     *  [TestNiaNetworkDataSource.latestChangeListVersion] method of our [TestNiaNetworkDataSource]
     *  property [network] for the [CollectionType.Topics] **matches** the `actual` [Int] which is
     *  the [ChangeListVersions.topicVersion] property of the [ChangeListVersions] object returned by
     *  the [Synchronizer.getChangeListVersions] method of our [Synchronizer] property [synchronizer].
     */
    @Test
    fun offlineFirstTopicsRepository_incremental_sync_pulls_from_network(): TestResult =
        testScope.runTest {
            // Set topics version to 10
            synchronizer.updateChangeListVersions {
                copy(topicVersion = 10)
            }

            subject.syncWith(synchronizer = synchronizer)

            val networkTopics: List<TopicEntity> = network.getTopics()
                .map(transform = NetworkTopic::asEntity)
                // Drop 10 to simulate the first 10 items being unchanged
                .drop(n = 10)

            val dbTopics: List<TopicEntity> = topicDao.getTopicEntities()
                .first()

            assertEquals(
                expected = networkTopics.map(transform = TopicEntity::id),
                actual = dbTopics.map(transform = TopicEntity::id),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(collectionType = CollectionType.Topics),
                actual = synchronizer.getChangeListVersions().topicVersion,
            )
        }

    /**
     * Test to verify that the [OfflineFirstTopicsRepository.syncWith] method deletes items that
     * have been marked as deleted on the network.
     *
     * We call the [TestScope.runTest] function of our [TestScope] property [testScope] to run its
     * [TestScope] `testBody` suspend lambda argument which is a lamdba in which we:
     *  - Initialize our [List] of [Topic] variable `val networkTopics` to the [List] of [Topic] that
     *  results from calling the [TestNiaNetworkDataSource.getTopics] method of our
     *  [TestNiaNetworkDataSource] property [network], mapping the [NetworkTopic] objects in that
     *  list to [TopicEntity] objects using the [NetworkTopic.asEntity] function, then mapping those
     *  [TopicEntity] objects to [Topic] objects using the [TopicEntity.asExternalModel] function.
     *  - We then initialize our [Set] of [String] variable `val deletedItems` to the [Set] of [String]
     *  that results from mapping the [Topic.id] of each [Topic] in `networkTopics`, then partitioning
     *  those [String]'s based on whether the `sum` of the `chars` of the `id` is an even number,
     *  taking the `first` of the [Pair] returned by [partition] (the [List] of `id` strings that
     *  satisfy the predicate) and converting that to a [Set].
     *  - We then iterate over each `id` [String] in `deletedItems` calling the
     *  [TestNiaNetworkDataSource.editCollection] method of our [TestNiaNetworkDataSource] property
     *  [network] to mark the item with that `id` in its [CollectionType.Topics] collection as
     *  `isDelete` true.
     *  - Sync the [OfflineFirstNewsRepository] property [subject] using the [Synchronizer] property
     *  [synchronizer].
     *  - Initialize our [List] of [Topic] variable `val dbTopics` to the [List] of [Topic] that
     *  results from feeding the [Flow] of [List] of [TopicEntity] returned by the
     *  [TopicDao.getTopicEntities] to [Flow.first] to collect the first [List] of [TopicEntity],
     *  then using its [Iterable.map] method with its `transform` argument [TopicEntity.asExternalModel]
     *  to convert each [TopicEntity] to a [Topic].
     *  - Call [assertEquals] to verify that the `expected` [List] of [String] which is produced by
     *  feeding the [List] of [Topic] variable `networkTopics` to an [Iterable.map] whose `transform`
     *  argument is the [Topic.id] property of each [Topic] minus the [Set] of [String] variable
     *  `deleted` **matches** the `actual` [List] of [String] that results from feeding the [List] of
     *  [String] variable `dbTopics` to an [Iterable.map] whose `transform` argument is the
     *  [Topic.id] property of each [Topic].
     *  - Call [assertEquals] to verify that the `expected` [Int] which is returned by the
     *  [TestNiaNetworkDataSource.latestChangeListVersion] for the `collectionType`
     *  [CollectionType.Topics] **matches** the `actual` [Int] which is the
     *  [ChangeListVersions.topicVersion] property of the [ChangeListVersions] object returned by
     *  the [Synchronizer.getChangeListVersions] method of our [Synchronizer] property [synchronizer].
     */
    @Test
    fun offlineFirstTopicsRepository_sync_deletes_items_marked_deleted_on_network(): TestResult =
        testScope.runTest {
            val networkTopics: List<Topic> = network.getTopics()
                .map(transform = NetworkTopic::asEntity)
                .map(transform = TopicEntity::asExternalModel)

            // Delete half of the items on the network
            val deletedItems: Set<String> = networkTopics
                .map(transform = Topic::id)
                .partition { it.chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.Topics,
                    id = it,
                    isDelete = true,
                )
            }

            subject.syncWith(synchronizer = synchronizer)

            val dbTopics: List<Topic> = topicDao.getTopicEntities()
                .first()
                .map(transform = TopicEntity::asExternalModel)

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                expected = networkTopics.map(transform = Topic::id) - deletedItems,
                actual = dbTopics.map(transform = Topic::id),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(collectionType = CollectionType.Topics),
                actual = synchronizer.getChangeListVersions().topicVersion,
            )
        }
}
