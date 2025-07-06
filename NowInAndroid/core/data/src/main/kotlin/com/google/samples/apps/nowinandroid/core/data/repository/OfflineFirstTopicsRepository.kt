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
import com.google.samples.apps.nowinandroid.core.database.dao.TopicDao
import com.google.samples.apps.nowinandroid.core.database.model.TopicEntity
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.datastore.ChangeListVersions
import com.google.samples.apps.nowinandroid.core.model.data.Topic
import com.google.samples.apps.nowinandroid.core.network.NiaNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Disk storage backed implementation of the [TopicsRepository].
 * Reads are exclusively from local storage to support offline access.
 *
 * @property topicDao dao to access topic db injected by HILT
 * @property network network data source injected by HILT
 */
internal class OfflineFirstTopicsRepository @Inject constructor(
    private val topicDao: TopicDao,
    private val network: NiaNetworkDataSource,
) : TopicsRepository {

    /**
     * Gets the available topics as a stream. We return the result of feeding the [Flow] of [List]
     * of [TopicEntity] returned by the [TopicDao.getTopicEntities] method of our [TopicDao] property
     * [topicDao] to its [Flow.map] method with its `transform` argument set to a lambda function
     * that accepts the [List] of [TopicEntity] passed the lambda in variable `it` and calls its
     * [Iterable.map] method with its `transform` argument the [TopicEntity.asExternalModel] method,
     * thereby transforming the [Flow] of [List] of [TopicEntity] into a [Flow] of [List] of
     * [Topic].
     */
    override fun getTopics(): Flow<List<Topic>> =
        topicDao.getTopicEntities()
            .map { it.map(transform = TopicEntity::asExternalModel) }

    /**
     * Gets data for a specific topic. We feed the [Flow] of [TopicEntity] returned by the
     * [TopicDao.getTopicEntity] method of our [TopicDao] property [topicDao] for the
     * `topicId` argument our [String] parameter [id] to its [Flow.map] method with its
     * `transform` argument set to a lambda function that accepts the [TopicEntity] passed the
     * lambda in variable `it` and calls its [TopicEntity.asExternalModel] method, thereby
     * transforming the [Flow] of [TopicEntity] into a [Flow] of [Topic].
     *
     * @param id id of the topic of interest
     */
    override fun getTopic(id: String): Flow<Topic> =
        topicDao.getTopicEntity(topicId = id).map { it.asExternalModel() }

    /**
     * Synchronizes the local database with the network. We return the [Boolean] result of calling
     * the [Synchronizer.changeListSync] method of our [Synchronizer] parameter [synchronizer] with
     * the following arguments:
     *  - `versionReader`: a reference to the [ChangeListVersions.topicVersion] property.
     *  - `changeListFetcher`: a lambda that accepts the [Int] passed the lambda in variable
     *  `currentVersion`, returns the result of calling the [NiaNetworkDataSource.getTopicChangeList]
     *  of our [NiaNetworkDataSource] property [network] with its `after` argument set to
     *  `currentVersion`.
     *  - `versionUpdater`: a [ChangeListVersions] lambda that accepts the [Int] passed the lambda
     *  in variable `latestVersion` and returns a copy of the [ChangeListVersions] receiver with its
     *  `topicVersion` property set to `latestVersion`.
     *  - `modelDeleter`: the [TopicDao.deleteTopics] method of our [TopicDao] property [topicDao].
     *  - `modelUpdater`: a lambda that accepts the [List] of [String] passed the lambda in variable
     *  `changedIds`, initializes the [List] of [NetworkTopic] variable `networkTopics` to the result
     *  of calling the [NiaNetworkDataSource.getTopics] method of our [NiaNetworkDataSource] property
     *  [network] with its `ids` argument set to `changedIds`. Then it calls the [TopicDao.upsertTopics]
     *  method of our [TopicDao] property [topicDao] with its `entities` argument set to the result
     *  of calling the [Iterable.map] method of the [List] of [NetworkTopic] variable `networkTopics`
     *  to transform each [NetworkTopic] to a [TopicEntity] using the [NetworkTopic.asEntity] method.
     *  The resulting [Boolean] is then returned.
     *
     * @param synchronizer The [Synchronizer] instance to use for syncing.
     * @return `true` if the sync was successful, `false` otherwise.
     */
    override suspend fun syncWith(synchronizer: Synchronizer): Boolean =
        synchronizer.changeListSync(
            versionReader = ChangeListVersions::topicVersion,
            changeListFetcher = { currentVersion: Int ->
                network.getTopicChangeList(after = currentVersion)
            },
            versionUpdater = { latestVersion: Int ->
                copy(topicVersion = latestVersion)
            },
            modelDeleter = topicDao::deleteTopics,
            modelUpdater = { changedIds: List<String> ->
                val networkTopics: List<NetworkTopic> = network.getTopics(ids = changedIds)
                topicDao.upsertTopics(
                    entities = networkTopics.map(transform = NetworkTopic::asEntity),
                )
            },
        )
}
