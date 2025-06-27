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
import com.google.samples.apps.nowinandroid.core.datastore.NiaPreferencesDataSource
import com.google.samples.apps.nowinandroid.core.datastore.UserPreferences
import com.google.samples.apps.nowinandroid.core.datastore.test.InMemoryDataStore
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkChangeList
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.notifications.Notifier
import com.google.samples.apps.nowinandroid.core.testing.notifications.TestNotifier
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
     * TODO: Continue here.
     */
    @Test
    fun offlineFirstNewsRepository_news_resources_stream_is_backed_by_news_resource_dao(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer)
            assertEquals(
                newsResourceDao.getNewsResources()
                    .first()
                    .map(PopulatedNewsResource::asExternalModel),
                subject.getNewsResources()
                    .first(),
            )
        }

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

    @Test
    fun offlineFirstNewsRepository_sync_pulls_from_network(): TestResult =
        testScope.runTest {
            // User has not onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(false)
            subject.syncWith(synchronizer)

            val newsResourcesFromNetwork = network.getNewsResources()
                .map(NetworkNewsResource::asEntity)
                .map(NewsResourceEntity::asExternalModel)

            val newsResourcesFromDb = newsResourceDao.getNewsResources()
                .first()
                .map(PopulatedNewsResource::asExternalModel)

            assertEquals(
                newsResourcesFromNetwork.map(NewsResource::id).sorted(),
                newsResourcesFromDb.map(NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(CollectionType.NewsResources),
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedNewsResources.isEmpty())
        }

    @Test
    fun offlineFirstNewsRepository_sync_deletes_items_marked_deleted_on_network(): TestResult =
        testScope.runTest {
            // User has not onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(false)

            val newsResourcesFromNetwork = network.getNewsResources()
                .map(NetworkNewsResource::asEntity)
                .map(NewsResourceEntity::asExternalModel)

            // Delete half of the items on the network
            val deletedItems = newsResourcesFromNetwork
                .map(NewsResource::id)
                .partition { it.chars().sum() % 2 == 0 }
                .first
                .toSet()

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.NewsResources,
                    id = it,
                    isDelete = true,
                )
            }

            subject.syncWith(synchronizer)

            val newsResourcesFromDb = newsResourceDao.getNewsResources()
                .first()
                .map(PopulatedNewsResource::asExternalModel)

            // Assert that items marked deleted on the network have been deleted locally
            assertEquals(
                expected = (newsResourcesFromNetwork.map(NewsResource::id) - deletedItems).sorted(),
                actual = newsResourcesFromDb.map(NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = network.latestChangeListVersion(CollectionType.NewsResources),
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedNewsResources.isEmpty())
        }

    @Test
    fun offlineFirstNewsRepository_incremental_sync_pulls_from_network(): TestResult =
        testScope.runTest {
            // User has not onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(false)

            // Set news version to 7
            synchronizer.updateChangeListVersions {
                copy(newsResourceVersion = 7)
            }

            subject.syncWith(synchronizer)

            val changeList = network.changeListsAfter(
                CollectionType.NewsResources,
                version = 7,
            )
            val changeListIds = changeList
                .map(NetworkChangeList::id)
                .toSet()

            val newsResourcesFromNetwork = network.getNewsResources()
                .map(NetworkNewsResource::asEntity)
                .map(NewsResourceEntity::asExternalModel)
                .filter { it.id in changeListIds }

            val newsResourcesFromDb = newsResourceDao.getNewsResources()
                .first()
                .map(PopulatedNewsResource::asExternalModel)

            assertEquals(
                expected = newsResourcesFromNetwork.map(NewsResource::id).sorted(),
                actual = newsResourcesFromDb.map(NewsResource::id).sorted(),
            )

            // After sync version should be updated
            assertEquals(
                expected = changeList.last().changeListVersion,
                actual = synchronizer.getChangeListVersions().newsResourceVersion,
            )

            // Notifier should not have been called
            assertTrue(notifier.addedNewsResources.isEmpty())
        }

    @Test
    fun offlineFirstNewsRepository_sync_saves_shell_topic_entities(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                expected = network.getNewsResources()
                    .map(NetworkNewsResource::topicEntityShells)
                    .flatten()
                    .distinctBy(TopicEntity::id)
                    .sortedBy(TopicEntity::toString),
                actual = topicDao.getTopicEntities()
                    .first()
                    .sortedBy(TopicEntity::toString),
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_saves_topic_cross_references(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                expected = network.getNewsResources()
                    .map(NetworkNewsResource::topicCrossReferences)
                    .flatten()
                    .distinct()
                    .sortedBy(NewsResourceTopicCrossRef::toString),
                actual = newsResourceDao.topicCrossReferences
                    .sortedBy(NewsResourceTopicCrossRef::toString),
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_marks_as_read_on_first_run(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer)

            assertEquals(
                network.getNewsResources().map { it.id }.toSet(),
                niaPreferencesDataSource.userData.first().viewedNewsResources,
            )
        }

    @Test
    fun offlineFirstNewsRepository_sync_does_not_mark_as_read_on_subsequent_run(): TestResult =
        testScope.runTest {
            // Pretend that we already have up to change list 7
            synchronizer.updateChangeListVersions {
                copy(newsResourceVersion = 7)
            }

            subject.syncWith(synchronizer)

            assertEquals(
                emptySet(),
                niaPreferencesDataSource.userData.first().viewedNewsResources,
            )
        }

    @Test
    fun offlineFirstNewsRepository_sends_notifications_for_newly_synced_news_that_is_followed(): TestResult =
        testScope.runTest {
            // User has onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(true)

            val networkNewsResources = network.getNewsResources()

            // Follow roughly half the topics
            val followedTopicIds = networkNewsResources
                .flatMap(NetworkNewsResource::topicEntityShells)
                .mapNotNull { topic ->
                    when (topic.id.chars().sum() % 2) {
                        0 -> topic.id
                        else -> null
                    }
                }
                .toSet()

            // Set followed topics
            niaPreferencesDataSource.setFollowedTopicIds(followedTopicIds)

            subject.syncWith(synchronizer)

            val followedNewsResourceIdsFromNetwork = networkNewsResources
                .filter { (it.topics intersect followedTopicIds).isNotEmpty() }
                .map(NetworkNewsResource::id)
                .sorted()

            // Notifier should have been called with only news resources that have topics
            // that the user follows
            assertEquals(
                expected = followedNewsResourceIdsFromNetwork,
                actual = notifier.addedNewsResources.first().map(NewsResource::id).sorted(),
            )
        }

    @Test
    fun offlineFirstNewsRepository_does_not_send_notifications_for_existing_news_resources(): TestResult =
        testScope.runTest {
            // User has onboarded
            niaPreferencesDataSource.setShouldHideOnboarding(true)

            val networkNewsResources = network.getNewsResources()
                .map(NetworkNewsResource::asEntity)

            val newsResources = networkNewsResources
                .map(NewsResourceEntity::asExternalModel)

            // Prepopulate dao with news resources
            newsResourceDao.upsertNewsResources(networkNewsResources)

            val followedTopicIds = newsResources
                .flatMap(NewsResource::topics)
                .map(Topic::id)
                .toSet()

            // Follow all topics
            niaPreferencesDataSource.setFollowedTopicIds(followedTopicIds)

            subject.syncWith(synchronizer)

            // Notifier should not have been called bc all news resources existed previously
            assertTrue(notifier.addedNewsResources.isEmpty())
        }
}
