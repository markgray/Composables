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

import com.google.samples.apps.nowinandroid.core.data.Synchronizer
import com.google.samples.apps.nowinandroid.core.data.changeListSync
import com.google.samples.apps.nowinandroid.core.data.model.asEntity
import com.google.samples.apps.nowinandroid.core.data.model.topicCrossReferences
import com.google.samples.apps.nowinandroid.core.data.model.topicEntityShells
import com.google.samples.apps.nowinandroid.core.database.dao.NewsResourceDao
import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.NewsResourceTopicCrossRef
import com.google.samples.apps.nowinandroid.core.database.model.PopulatedNewsResource
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.datastore.ChangeListVersions
import com.google.samples.apps.nowinandroid.core.datastore.NiaPreferencesDataSource
import com.google.samples.apps.nowinandroid.core.model.data.NewsResource
import com.google.samples.apps.nowinandroid.core.model.data.UserData
import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkNewsResource
import com.google.samples.apps.nowinandroid.core.notifications.Notifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Heuristic value to optimize for serialization and deserialization cost on client and server
 * for each news resource batch.
 */
private const val SYNC_BATCH_SIZE = 40

/**
 * Disk storage backed implementation of the [NewsRepository].
 * Reads are exclusively from local storage to support offline access.
 *
 * @property niaPreferencesDataSource disk storage for local data injected by HILT
 * @property newsResourceDao DAO for news resources injected by HILT
 * @property topicDao DAO for topics injected by HILT
 * @property network disk storage for network data injected by HILT
 * @property notifier notifications for news resources injected by HILT
 */
internal class OfflineFirstNewsRepository @Inject constructor(
    private val niaPreferencesDataSource: NiaPreferencesDataSource,
    private val newsResourceDao: NewsResourceDao,
    private val topicDao: TopicDao,
    private val network: NiaNetworkDataSource,
    private val notifier: Notifier,
) : NewsRepository {

    /**
     * Gets the available news resources as a stream. We return the result of feeding the
     * [Flow] of [List] of [PopulatedNewsResource] returned by the [NewsResourceDao.getNewsResources]
     * method of our [NewsResourceDao] property [newsResourceDao] called with its `useFilterTopicIds`
     * `true` if the [NewsResourceQuery.filterTopicIds] of our [NewsResourceQuery] parameter [query]
     * is not `null`, and with its `filterTopicIds` the value of the [NewsResourceQuery.filterTopicIds]
     * property of our [NewsResourceQuery] parameter [query] or an empty set if it is `null`, with its
     * `useFilterNewsIds` `true` if the [NewsResourceQuery.filterNewsIds] of our [NewsResourceQuery]
     * parameter [query] is not `null`, and with its `filterNewsIds` the value of the
     * [NewsResourceQuery.filterNewsIds] property of our [NewsResourceQuery] parameter [query] if it
     * is not `null`, or an empty set if it is `null`. This is fed to its [Flow.map] method in whose
     * `transform` block we call the [Iterable.map] method of the [List] of [PopulatedNewsResource]
     * passed the lambda to transform each [PopulatedNewsResource] to a [NewsResource] using the
     * [PopulatedNewsResource.asExternalModel] method. The resulting [Flow] of [List] of [NewsResource]
     * is then returned.
     *
     * @param query - A [NewsResourceQuery] query to filter the available news resources
     * @return A [Flow] of [List] of [NewsResource]
     */
    override fun getNewsResources(
        query: NewsResourceQuery,
    ): Flow<List<NewsResource>> = newsResourceDao.getNewsResources(
        useFilterTopicIds = query.filterTopicIds != null,
        filterTopicIds = query.filterTopicIds ?: emptySet(),
        useFilterNewsIds = query.filterNewsIds != null,
        filterNewsIds = query.filterNewsIds ?: emptySet(),
    )
        .map { it.map(transform = PopulatedNewsResource::asExternalModel) }

    /**
     * Synchronizes the local news resources with the network.
     *
     * This function fetches the latest news resource changes from the network and updates the
     * local database accordingly. It also handles the following:
     *  - Deleting news resources that are no longer available on the network.
     *  - Updating existing news resources with new data.
     *  - Adding new news resources to the local database.
     *  - Marking all news resources as viewed on the first sync.
     *  - Sending notifications for newly added news resources if the user has completed onboarding.
     *
     * We start by initializing our [Boolean] variable `isFirstSync` to `false`. Then we return the
     * result of calling the [Synchronizer.changeListSync] method of our [Synchronizer] parameter
     * [synchronizer] with the following arguments:
     *  - `versionReader`: a reference to the [ChangeListVersions.newsResourceVersion] property.
     *  - `changeListFetcher`: a lambda that accepts the [Int] passed the lambda in variable
     *  `currentVersion`, sets `isFirstSync` to `true` if `currentVersion` is less than or equal to
     *  `0`, then returns the result of calling the [NiaNetworkDataSource.getNewsResourceChangeList]
     *  of our [NiaNetworkDataSource] property [network] with its `after` argument set to
     *  `currentVersion`.
     *  - `versionUpdater`: a [ChangeListVersions] lambda that accepts the [Int] passed the lambda
     *  in variable `latestVersion` and returns a copy of the [ChangeListVersions] receiver with its
     *  `newsResourceVersion` property set to `latestVersion`.
     *  - `modelDeleter`: the [NewsResourceDao.deleteNewsResources] method of our [NewsResourceDao]
     *  property [newsResourceDao].
     *  - `modelUpdater`: a lambda that accepts the [List] of [String] passed the lambda in variable
     *  `changedIds`, initializes the [UserData] variable `userData` to the result of calling the
     *  [Flow.first] method of our [NiaPreferencesDataSource.userData] property of our
     *  [NiaPreferencesDataSource] property [niaPreferencesDataSource], initializes the [Boolean]
     *  variable `hasOnboarded` to the value of the [UserData.shouldHideOnboarding] property of
     *  `userData`, initializes the [Set] variable `followedTopicIds` to the value of the
     *  [UserData.followedTopics] property of `userData`, initializes the [Set] of [String] variable
     *  `existingNewsResourceIdsThatHaveChanged` to the result of calling the
     *  [NewsResourceDao.getNewsResourceIds] method of our [NewsResourceDao] property [newsResourceDao]
     *  with its `useFilterTopicIds` argument set to `true`, its `filterTopicIds` argument set to
     *  `followedTopicIds`, its `useFilterNewsIds` argument set to `true`, and its `filterNewsIds`
     *  argument set to `changedIds` converted to a [Set], then calling the [Flow.first] method of
     *  the resulting [Flow] to await the first value and then converting it to a [Set] **if**
     *  `hasOnboarded` is `true`, otherwise an empty set. Then if `isFirstSync` is `true`, we call
     *  the [NiaPreferencesDataSource.setNewsResourcesViewed] method of our [NiaPreferencesDataSource]
     *  property [niaPreferencesDataSource] with its `newsResourceIds` argument set to `changedIds`
     *  and its `viewed` argument set to `true`. Next we call the [Iterable.chunked] method of [List]
     *  of [String] variable `changedIds` with its `size` argument set to `SYNC_BATCH_SIZE` to obtain
     *  a [List] of [List] of [String]s. We then iterate over each [List] of [String]s using the
     *  [Iterable.forEach] method accepting each [List] of [String] in variable `chunkedIds`. We then
     *  initialize our [List] of [NetworkNewsResource] variable `networkNewsResources` to the result
     *  of calling the [NiaNetworkDataSource.getNewsResources] method of our [NiaNetworkDataSource]
     *  property [network] with its `ids` argument set to `chunkedIds`. Next we call the
     *  [TopicDao.insertOrIgnoreTopics] method of our [TopicDao] property [topicDao] with its
     *  `topicEntities` argument set to the result of calling the [Iterable.map] method of the
     *  [List] of [NetworkNewsResource] variable `networkNewsResources` to transform each
     *  [NetworkNewsResource] to a [TopicEntity] using the [NetworkNewsResource.topicEntityShells],
     *  we use the [Iterable.flatten] method to flatten the resulting [List] of [List] of
     *  [TopicEntity]s, into a [List] of [TopicEntity]s, and then call the [Iterable.distinctBy]
     *  method to remove any duplicate [TopicEntity]s. Next we call the
     *  [NewsResourceDao.upsertNewsResources] method of our [NewsResourceDao] property [newsResourceDao]
     *  with its `newsResourceEntities` argument set to the result of calling the [Iterable.map]
     *  method of the [List] of [NetworkNewsResource] variable `networkNewsResources` to transform
     *  each [NetworkNewsResource] to a [NewsResourceEntity] using the [NetworkNewsResource.asEntity]
     *  method. Next we call the [NewsResourceDao.insertOrIgnoreTopicCrossRefEntities] method of our
     *  [NewsResourceDao] property [newsResourceDao] with its `newsResourceTopicCrossReferences`
     *  argument set to the result of calling the [Iterable.map] method of the [List] of
     *  [NetworkNewsResource] variable `networkNewsResources` to transform each [NetworkNewsResource]
     *  to a [List] of [NewsResourceTopicCrossRef]s using the [NetworkNewsResource.topicCrossReferences],
     *  then use the [Iterable.distinct] method to remove any duplicate [NewsResourceTopicCrossRef]s,
     *  and then call the [Iterable.flatten] method to flatten the resulting [List] of [List] of
     *  [NewsResourceTopicCrossRef]s into a [List] of [NewsResourceTopicCrossRef]s. Then if
     *  `hasOnboarded` is `true`, we initialize our [List] of [NewsResource] variable
     *  `addedNewsResources` to the result of calling the [NewsResourceDao.getNewsResources] method
     *  of our [NewsResourceDao] property [newsResourceDao] with its `useFilterTopicIds` argument set
     *  to `true`, its `filterTopicIds` argument set to `followedTopicIds`, its `useFilterNewsIds`
     *  argument set to `true`, and its `filterNewsIds` argument set to the result of calling the
     *  [Iterable.toSet] method of the [List] of [String] variable `changedIds` minus the [Set] of
     *  [String] variable `existingNewsResourceIdsThatHaveChanged`. Then if `addedNewsResources` is
     *  not empty, we call the [Notifier.postNewsNotifications] method of our [Notifier] property
     *  [notifier] with its `newsResources` argument set to `addedNewsResources`.
     *
     * @param synchronizer The [Synchronizer] instance to use for syncing.
     * @return `true` if the sync was successful, `false` otherwise.
     */
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean {
        var isFirstSync = false
        return synchronizer.changeListSync(
            versionReader = ChangeListVersions::newsResourceVersion,
            changeListFetcher = { currentVersion: Int ->
                isFirstSync = currentVersion <= 0
                network.getNewsResourceChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion: Int ->
                copy(newsResourceVersion = latestVersion)
            },
            modelDeleter = newsResourceDao::deleteNewsResources,
            modelUpdater = { changedIds: List<String> ->
                val userData: UserData = niaPreferencesDataSource.userData.first()
                val hasOnboarded: Boolean = userData.shouldHideOnboarding
                val followedTopicIds: Set<String> = userData.followedTopics

                val existingNewsResourceIdsThatHaveChanged: Set<String> = when {
                    hasOnboarded -> newsResourceDao.getNewsResourceIds(
                        useFilterTopicIds = true,
                        filterTopicIds = followedTopicIds,
                        useFilterNewsIds = true,
                        filterNewsIds = changedIds.toSet(),
                    )
                        .first()
                        .toSet()
                    // No need to retrieve anything if notifications won't be sent
                    else -> emptySet()
                }

                if (isFirstSync) {
                    // When we first retrieve news, mark everything viewed, so that we aren't
                    // overwhelmed with all historical news.
                    niaPreferencesDataSource.setNewsResourcesViewed(
                        newsResourceIds = changedIds,
                        viewed = true,
                    )
                }

                // Obtain the news resources which have changed from the network and upsert them locally
                changedIds.chunked(size = SYNC_BATCH_SIZE).forEach { chunkedIds: List<String> ->
                    val networkNewsResources: List<NetworkNewsResource> =
                        network.getNewsResources(ids = chunkedIds)

                    // Order of invocation matters to satisfy id and foreign key constraints!

                    topicDao.insertOrIgnoreTopics(
                        topicEntities = networkNewsResources
                            .map(transform = NetworkNewsResource::topicEntityShells)
                            .flatten()
                            .distinctBy(TopicEntity::id),
                    )
                    newsResourceDao.upsertNewsResources(
                        newsResourceEntities = networkNewsResources.map(
                            transform = NetworkNewsResource::asEntity,
                        ),
                    )
                    newsResourceDao.insertOrIgnoreTopicCrossRefEntities(
                        newsResourceTopicCrossReferences = networkNewsResources
                            .map(transform = NetworkNewsResource::topicCrossReferences)
                            .distinct()
                            .flatten(),
                    )
                }

                if (hasOnboarded) {
                    val addedNewsResources: List<NewsResource> = newsResourceDao.getNewsResources(
                        useFilterTopicIds = true,
                        filterTopicIds = followedTopicIds,
                        useFilterNewsIds = true,
                        filterNewsIds = changedIds.toSet() - existingNewsResourceIdsThatHaveChanged,
                    )
                        .first()
                        .map(transform = PopulatedNewsResource::asExternalModel)

                    if (addedNewsResources.isNotEmpty()) {
                        notifier.postNewsNotifications(
                            newsResources = addedNewsResources,
                        )
                    }
                }
            },
        )
    }
}
