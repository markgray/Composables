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
     * called with a [NewsResourceQuery] that specifies a `filterTopicIds` set, the returned stream
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
     * TODO: Continue here.
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

            deletedItems.forEach {
                network.editCollection(
                    collectionType = CollectionType.NewsResources,
                    id = it,
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

    @Test
    fun offlineFirstNewsRepository_sync_marks_as_read_on_first_run(): TestResult =
        testScope.runTest {
            subject.syncWith(synchronizer = synchronizer)

            assertEquals(
                expected = network.getNewsResources().map { it.id }.toSet(),
                actual = niaPreferencesDataSource.userData.first().viewedNewsResources,
            )
        }

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
